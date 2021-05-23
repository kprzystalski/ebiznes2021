import {observable} from "mobx";

const signInFormStore = observable({
    email: '',
    password: ''
});

export default signInFormStore;
