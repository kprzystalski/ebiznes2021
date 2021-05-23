import React, {Component} from "react";
import M from "materialize-css";
import "materialize-css/dist/css/materialize.min.css";
import SignUpForm from "../SignUpForm/SignUpForm";

class SignUpModal extends Component {

    componentDidMount() {
        M.Modal.init(this.Modal);
    }

    closeModal = () => {
        M.Modal.getInstance(this.Modal).close();
    }

    render() {
        const formId = "sign_up_form";
        const modalId = "sign_up_modal";
        const modalHref = "#" + modalId;

        return (
            <div>
                <a href={modalHref} className="modal-trigger">
                    Sign up
                </a>

                <div ref={Modal => this.Modal = Modal} id={modalId} className="modal authentication-modal">
                    <div className="modal-content">
                        <SignUpForm formId={formId} closeModal={this.closeModal}/>
                    </div>

                    <div className="modal-footer small-margin-bottom">
                        <button className="btn-flat" type="reset" form={formId} onClick={this.closeModal}>Cancel
                        </button>
                        <button className="waves-effect waves-light btn" type="submit" form={formId}>
                            Sign up
                        </button>
                    </div>
                </div>
            </div>
        );
    }
}

export default SignUpModal;
