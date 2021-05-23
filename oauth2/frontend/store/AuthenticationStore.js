import {decorate, observable, runInAction} from "mobx";
import AuthenticationService from "../service/AuthenticationService";

class AuthenticationStore {
    constructor() {
        this.authenticationService = new AuthenticationService();
    }

    signUpStatus = 'initial';
    signInStatus = 'initial';
    loggedIn;

    signUpAsync = async (model) => {
        try {
            const response = await this.authenticationService.signUp(model);
            if (response.status === 201) {
                runInAction(() => {
                    this.signUpStatus = "success";
                })
            } else {
                runInAction(() => {
                    this.signUpStatus = "error";
                })
            }
        } catch (error) {
            runInAction(() => {
                this.signUpStatus = "error";
            });
        }
    }

    signInAsync = async (model) => {
        try {
            const response = await this.authenticationService.signIn(model);
            if (response.status === 200) {
                runInAction(() => {
                    this.loggedIn = true;
                    this.signInStatus = "success";
                })
            } else {
                runInAction(() => {
                    this.loggedIn = false;
                    this.signInStatus = "error";
                })
            }
        } catch (error) {
            runInAction(() => {
                this.signInStatus = "error";
            });
        }
    }

    signOutAsync = async () => {
        try {
            const response = await this.authenticationService.signOut();
            if (response.status === 200) {
                runInAction(() => {
                    this.loggedIn = false;
                    this.status = "success";
                })
            }
        } catch (error) {
            runInAction(() => {
                this.status = "error";
            });
        }
    }
}

decorate(AuthenticationStore, {
    loggedIn: observable,
    signUpStatus: observable,
    signInStatus: observable
})

export default new AuthenticationStore();
