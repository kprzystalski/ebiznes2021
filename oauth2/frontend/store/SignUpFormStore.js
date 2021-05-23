import {observable} from "mobx";

const signUpFormStore = observable({
    email: '',
    password: ''
});

export default signUpFormStore;
