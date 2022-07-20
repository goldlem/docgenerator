import React from "react";
import Select from "react-select";
import { getDownloadFile } from "./file";
import { saveAs } from "file-saver";

export const HtmlForm =() => {
    const templateOptions = [
        { label: "Template1", value: "template1" },
        { label: "Template2", value: "template2" },
        { label: "Template3", value: "template3" },
        { label: "Template4", value: "template4" },
    ];

    const [template, setTemplate] = React.useState(templateOptions);
    const [documentNumber, setDocNumber] = React.useState(1);

    const handleChange = (e, setter) => {
        if (Array.isArray(e)) {
            setter(e);
        } else {
            setter(e.target.value)
        }
    }

    const downloadFile = () => {
        if (documentNumber > 0) {

            const obj = {
                "template": template.map((x) => x.value),
                "documentNumber": parseInt(documentNumber),
            }
            getDownloadFile("http://localhost:8080/invoice/html",JSON.stringify(obj)).then(blob => saveAs(blob, 'file.zip'))
                .catch((error) => {
                    console.log(error);
                });
        } else { alert(" number of documents cannot be <= 0"); }
    }

    return (
        <div className='html-form'>
            <form>
                <Select
                    className="dropdown"
                    placeholder="Select template type"
                    value={template}
                    options={templateOptions}
                    onChange={(event) => handleChange(event, setTemplate)}
                    isMulti
                    isClearable
                />
                <br />
                <label htmlFor="doc-number-input">Enter number of documents</label>
                <input
                    className="css-1s2u09g-control"
                    id="doc-number-input"
                    style={{ width: "100%", textAlign: "center" }}
                    placeholder="Select number of files"
                    type={"number"}
                    value={documentNumber}
                    onChange={(event) => handleChange(event, setDocNumber)}
                    min={0}
                />
                <br />

                <button type='button' onClick={downloadFile}>Download</button>
            </form>
        </div>
    )

}