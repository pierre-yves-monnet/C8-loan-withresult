// -----------------------------------------------------------
//
// Definition
//
// List of all runners available
//
// -----------------------------------------------------------

import React from 'react';
import {Card} from "react-bootstrap";

import RestCallService from "../services/RestCallService";
import {Button, TextInput} from "carbon-components-react";
import {ArrowRepeat} from "react-bootstrap-icons";
import logo from "../logo.svg";

class Loan extends React.Component {


    constructor(_props) {
        super();

        this.state = {
            loans: [],
            newLoan: {
                processInstanceId: "",
                amount: 3400,
                ssn: "114-23-1850",
                status: {}
            },
            loanInfo : {ssn: "", result:{}},
            display: {
                loading: false
            },
        };
    }

    componentDidMount() {
        this.refreshLoans();
    }

    /*           {JSON.stringify(this.state.runners, null, 2) } */
    render() {
        return (
            <div className="container">

                <h1>
                    <img src="/img/Cranberries.png" style={{ width: '50px', height: '50px' }} alt="logo"  />
                    Cranberry Loan Application</h1>
                <div className="row" style={{width: "100%"}}>
                    <Card>
                        <Card.Header style={{backgroundColor: "rgba(0,0,0,.03)"}}>New loan</Card.Header>
                        <Card.Body>
                            <div className="row" style={{width: "100%"}}>

                                <div className="col-md-4">
                                    <TextInput
                                        className="m-3"
                                        id="explicitId"
                                        labelText="Amount of loan"
                                        value={this.state.newLoan.amount}
                                        type="number"
                                        onChange={(event) => {
                                            var parameter = this.state.newLoan;
                                            parameter.amount = event.target.value;
                                            this.setState({"newLoan": parameter})
                                        }}
                                        placeholder="Enter the amount of loan"
                                    />
                                </div>
                                <div className="col-md-4">
                                    <TextInput
                                        className="m-3"
                                        id="ssn"
                                        labelText="SSN"
                                        value={this.state.newLoan.ssn}
                                        onChange={(event) => {
                                            var parameter = this.state.newLoan;
                                            parameter.ssn = event.target.value;
                                            this.setState({"newLoan": parameter})
                                        }}
                                        placeholder="Enter the amount of loan"
                                    />
                                </div>
                                <div className="col-md-4">
                                    SSN: &lt;age&gt;-11-&lt;CreditScore&gt;<br/>
                                    Age: firstSSN % 50 +20<br/>
                                    CreditScore: max(LastSSN % 1000, 300)<br/>
                                    ACCEPTED: CreditScore &gt; 800 AND amount &gt; 15000<br/>
                                    REJECTED: CreditScore &lt; 500<br/>
                                    REVIEW: All other situations
                                </div>
                            </div>
                            <div className="row" style={{width: "100%"}}>
                                <Button className="btn btn-info btn-sm"
                                        onClick={() => {
                                            this.createLoan()
                                        }}
                                        disabled={this.state.display.loading}>
                                    Create loan
                                </Button>
                            </div>
                            <div className="row" style={{width: "100%"}}>
                                <pre>{JSON.stringify(this.state.newLoan.status, null, 2)}</pre>
                            </div>
                        </Card.Body>
                    </Card>
                </div>

                <div className="row" style={{width: "100%", marginTop: "10px"}}>
                    <Card>
                        <Card.Header style={{
                            backgroundColor: "rgba(0,0,0,.03)",
                            display: "flex",
                            justifyContent: "space-between",
                            alignItems: "center"
                        }}>Loans
                            <Button className="btn btn-success btn-sm"
                                    onClick={() => {
                                        this.refreshLoans()
                                    }}
                                    disabled={this.state.display.loading}>
                                <ArrowRepeat/> Refresh
                            </Button>
                        </Card.Header>
                        <Card.Body>

                            <table id="runnersTable" className="table is-hoverable is-fullwidth">
                                <thead>
                                <tr>
                                    <th>Loan Reference</th>
                                    <th>LastName</th>
                                    <th>SSN</th>
                                    <th>Amount</th>
                                    <th>Status (request)</th>
                                    <th>Status (loan)</th>
                                    <th>Internal Message</th>
                                    <th></th>
                                </tr>
                                </thead>
                                <tbody>
                                {this.state.loans ? this.state.loans.map((item, _index) =>
                                    <tr>
                                        <td>{item.loanId}</td>
                                        <td>{item.lastName}</td>
                                        <td>{item.ssn}</td>
                                        <td>{item.amount}</td>
                                        <td>{item.statusRequest}</td>
                                        <td>{item.statusLoan}</td>
                                        <td>{item.messageInternal}</td>
                                        <td>
                                            {item.statusLoan === "REVIEW" ? <div>
                                                <Button className="btn btn-success btn-sm  me-2"
                                                        onClick={() => {
                                                            this.reviewLoan(item.loanId, true)
                                                        }}
                                                        disabled={this.state.display.loading}>
                                                    Accept loan
                                                </Button>
                                                <Button className="btn btn-danger btn-sm"
                                                        onClick={() => {
                                                            this.reviewLoan(item.loanId, false)
                                                        }}
                                                        disabled={this.state.display.loading}>
                                                    Cancel loan
                                                </Button>
                                            </div> : <div/>}
                                        </td>
                                    </tr>
                                ) : <div/>}
                                </tbody>
                            </table>
                        </Card.Body>
                    </Card>

                </div>
                <div className="row" style={{width: "100%", marginTop: "10px"}}>
                    <Card>
                        <Card.Header style={{backgroundColor: "rgba(0,0,0,.03)"}}>Loan information</Card.Header>
                        <Card.Body>
                            <div className="row" style={{width: "100%"}}>

                                <div className="col-md-4">
                                    You must give a existing SSN, waiting for a review. Complete loan are not accessible via this function.
                                    <TextInput
                                        className="m-3"
                                        id="SSNID"
                                        labelText="SSN"
                                        value={this.state.loanInfo.ssn}
                                        onChange={(event) => {
                                            var parameter = this.state.loanInfo;
                                            parameter.ssn = event.target.value;
                                            this.setState({"loanInfo": parameter})
                                        }}
                                        placeholder="Enter the loan ID"
                                    />
                                </div>

                            </div>
                            <div className="row" style={{width: "100%"}}>
                                <Button className="btn btn-info btn-sm"
                                        onClick={() => {
                                            this.getLoanInfo(this.state.loanInfo.ssn)
                                        }}
                                        disabled={this.state.display.loading}>
                                    Get Loan Info
                                </Button>
                            </div>
                            <div className="row" style={{width: "100%"}}>
                                <pre>{JSON.stringify(this.state.loanInfo.result.loanInfo, null, 2)}</pre>
                            </div>
                        </Card.Body>
                    </Card>
                </div>
            </div>
        );
    }

    createLoan() {
        console.log("Definition.refreshList http[loan/applyLoan]");
        var newLoan = this.state.newLoan;
        newLoan.status = "Please wait...";
        this.setState({newLoan: newLoan});

        var restCallService = RestCallService.getInstance();
        restCallService.postJson('loan/applyLoan?details=true', this.state.newLoan, this, this.createLoanCallback);
    }

    createLoanCallback(httpPayload) {
        if (httpPayload.isError()) {
            this.setState({status: "Error"});
        } else {
            var newLoan = this.state.newLoan;
            newLoan.status = httpPayload.getData();
            this.setState({newLoan: newLoan});
            this.refreshLoans();

        }
    }

    refreshLoans() {
        console.log("Definition.refreshList http[loan/list]");
        this.setState({loans: [], loanCreationInfo: ""});
        var restCallService = RestCallService.getInstance();
        restCallService.getJson('loan/list?details=true', this, this.refreshLoansCallback);
    }

    refreshLoansCallback(httpPayload) {
        if (httpPayload.isError()) {
            this.setState({status: "Error"});
        } else {
            this.setState({loans: httpPayload.getData()});

        }
    }


    reviewLoan(loanId, decision) {
        console.log("Loan.accepLoat http[loan/review?accept="+decision+"&loan="+loanId+"]");
        this.setState({statusOperation: "Accepting loan " + loanId + "..."});

        var param={"decision": decision, "loanId": loanId};
        var restCallService = RestCallService.getInstance();
        restCallService.putJson('loan/review?a=1', param, this, this.reviewLoanCallback);
    }

    reviewLoanCallback(httpPayload) {
        if (httpPayload.isError()) {
            this.setState({status: "Can't access loan"});
        } else {
            var listLoansUpdated = this.state.loans.map(loan => loan.loanId === httpPayload.getData().loanId ? httpPayload.getData() : loan);
            this.setState({loans: listLoansUpdated});
        }
    }




    getLoanInfo(ssn) {
        console.log("Loan.getLoanInfo http[loan/info?ssn="+ssn+"]");

        var restCallService = RestCallService.getInstance();
        restCallService.getJson('loan/info?ssn='+ssn, this, this.getLoanInfoCallback);
    }

    getLoanInfoCallback(httpPayload) {
        if (httpPayload.isError()) {
            this.setState({statusOperation: "Can't access loan"});
        } else {
            var loanInfoResult = this.state.loanInfo;
            loanInfoResult.result = httpPayload.getData();
            this.setState({loanInfo: loanInfoResult});
        }
    }
    /**
     * Set the display property
     * @param propertyName name of the property
     * @param propertyValue the value
     */
    setDisplayProperty = (propertyName, propertyValue) => {
        let displayObject = this.state.display;
        displayObject[propertyName] = propertyValue;
        this.setState({display: displayObject});
    }

}

export default Loan;
