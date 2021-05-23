import React, {Component} from 'react';
import M from "materialize-css";
import {inject, observer} from "mobx-react";

class SignUpForm extends Component {

    componentDidMount() {
        M.Range.init(this.Range);
    }

    signUp = (e) => {
        e.preventDefault();
        const request = {
            email: this.props.signUpFormStore.email,
            password: this.props.signUpFormStore.password
        };
        e.persist();
        this.props.authenticationStore.signUpAsync(request)
            .then(() => {
                if (this.props.authenticationStore.signUpStatus === "success") {
                    this.clear(e);
                    this.props.closeModal();
                    M.toast({
                        html: 'Thank you for registration!',
                        classes: 'rounded center-align teal lighten-2',
                        displayLength: 2000
                    })
                } else {
                    M.toast({
                        html: 'User with this e-mail already exists!',
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
        this.props.signUpFormStore[key] = value
    }

    onChange = (event) => {
        this.updateProperty(event.target.name, event.target.value)
    }

    render() {
        return (
            <form onSubmit={this.signUp} onReset={this.clear} id={this.props.formId}>
                <div className="row">
                    <div className="input-field col s12">
                        <input id="sign_up_email" name="email" type="text" className="validate"
                               onChange={this.onChange} required aria-required="true"/>
                        <label htmlFor="sign_up_email" data-error="wrong" data-success="right">E-mail</label>
                    </div>
                </div>

                <div className=" row">
                    <div className=" input-field col s12">
                        <input id="sign_up_password" name="password" type="password" className="validate"
                               onChange={this.onChange} required aria-required="true"/>
                        <label htmlFor="sign_up_password">Password</label>
                    </div>
                </div>
            </form>
        );
    }
}

export default inject('signUpFormStore', 'authenticationStore')(observer(SignUpForm))
