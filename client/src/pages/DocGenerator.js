import React, { Fragment } from "react";
import { Link } from "react-router-dom";
import { DocForm } from "../custom/DocForm";
import './style.css'

export const DocGenerator = () => {
    return (
        <Fragment>
            <Link to={"/"}>Home</Link>
            <div className="wrapper">
                {DocForm()}
            </div>
        </Fragment>
    )
}