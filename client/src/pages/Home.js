import React, { Fragment } from "react";
import { Link } from "react-router-dom";

export const Home = () => {
    return (
        <Fragment>
            <div>
                    <Link to={"/doc"}>Generate doc</Link>
                <br/>
                    <Link to={"/html"}>Generate html</Link>
             
            </div>
        </Fragment>
    )
}