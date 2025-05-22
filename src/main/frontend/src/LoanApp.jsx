// -----------------------------------------------------------
//
// PeaApps
//
// Manage the main application
//
// -----------------------------------------------------------

import React from 'react';
import './index.scss';

import 'bootstrap/dist/css/bootstrap.min.css';
import Loan from "./loan/Loan";


class LoanApp extends React.Component {


    constructor(_props) {
        super();
        this.state = {
           loans:[],
            info: {
                version: ""
            }
        };
    }

    componentDidMount() {
    }

    render() {
        return (
            <div>
              <Loan/>
            </div>);
    }



}

export default LoanApp;


