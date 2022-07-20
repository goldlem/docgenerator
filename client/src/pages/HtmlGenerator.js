import React, { Fragment } from "react";
import { Link } from "react-router-dom";
import { HtmlForm } from "../custom/HtmlForm";

export const HtmlGenerator = () => {
    return (
        <Fragment>
            <Link to={"/"}>Home</Link>
            <div className="wrapper">
            <HtmlForm/>
            </div>
        </Fragment>
    )
}