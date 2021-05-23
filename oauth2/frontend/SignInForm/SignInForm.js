import React, {Component} from 'react';
import M from "materialize-css";
import {inject, observer} from "mobx-react";

class SignInForm extends Component {

    componentDidMount() {
        M.Range.init(this.Range);
    }

    signIn = (e) => {
        e.preventDefault();
        const request = {
            email: this.props.signInFormStore.email,
            password: this.props.signInFormStore.password
        };
        e.persist();
        this.props.authenticationStore.signInAsync(request)
            .then(() => {
                if (this.props.authenticationStore.signInStatus === 'success') {
                    this.clear(e);
                    M.toast({
                        html: 'Logged in!',
                        classes: 'rounded center-align teal lighten-2',
                        displayLength: 2000
                    })
                } else {
                    M.toast({
                        html: 'Invalid credentials!',
                        classes: 'rounded center-align red lighten-2',
                        displayLength: 2000
                    })
                }
            })
    }

    clear = (e) => {
        e.target.reset();
    }

    updateProperty = (key, value) => {
        this.props.signInFormStore[key] = value
    }

    onChange = (event) => {
        this.updateProperty(event.target.name, event.target.value)
    }

    render() {
        return (
            <form onSubmit={this.signIn} onReset={this.clear} id={this.props.formId}>
                <div className="row">
                    <div className="input-field col s12">
                        <input id="sign_in_email" name="email" type="text" className="validate"
                               onChange={this.onChange} required aria-required="true"/>
                        <label htmlFor="sign_in_email" data-error="wrong" data-success="right">E-mail</label>
                    </div>
                </div>

                <div className=" row">
                    <div className=" input-field col s12">
                        <input id="sign_in_password" name="password" type="password" className="validate"
                               onChange={this.onChange} required aria-required="true"/>
                        <label htmlFor="sign_in_password">Password</label>
                    </div>
                </div>
            </form>
        );
    }
}

export default inject('signInFormStore', 'authenticationStore')(observer(SignInForm))
