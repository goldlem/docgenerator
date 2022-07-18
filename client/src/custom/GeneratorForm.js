import '../GeneratorForm.css';
import Select from "react-select";
import React from "react";
import { getDownloadFile } from "./file"
import { saveAs } from "file-saver";

export function Generate() {

    const invoiceOptions = [
        { label: "Invoice1", value: "invoice1" },
        { label: "Invoice2", value: "invoice2" },
        { label: "Invoice3", value: "invoice3" },
        { label: "Invoice4", value: "invoice4" },
        { label: "InvoiceWithRemittance1", value: "inv_with_rem1" },
        { label: "InvoiceWithRemittance2", value: "inv_with_rem2" },
    ];

    const remittanceOptions = [
        { label: "Remittance1", value: "remittance1" },
        { label: "Remittance2", value: "remittance2" },
        { label: "Remittance3", value: "remittance3" },
        { label: "Remittance4", value: "remittance4" },
        { label: "Remittance5", value: "remittance5" },
        { label: "Remittance6", value: "remittance6" },
    ];

    const extensionOptions = [
        { label: "PDF", value: "pdf" },
        { label: "HTML", value: "html" },
        { label: "DOCX", value: "docx" },
    ];

    const [invoiceType, setInvoiceType] = React.useState(invoiceOptions);
    const [remittanceType, setRemittanceType] = React.useState(remittanceOptions);
    const [fileExtension, setFileExtension] = React.useState(extensionOptions.filter((x) => x.value === "pdf"));
    const [docNumber, setDocNumber] = React.useState(1);

    const handleChange = (e, setter) => {
        if (Array.isArray(e)) {
            setter(e);
        } else {
            setter(e.target.value)
        }
    }

    const downloadFile = () => {
        if (docNumber > 0) {

            const obj = {
                "invoiceType": invoiceType.map((x) => x.value),
                "remittanceType": remittanceType.map((x) => x.value),
                "fileExtension": fileExtension.map((x) => x.value),
                "docNumber": parseInt(docNumber),
            }
            getDownloadFile(JSON.stringify(obj)).then(blob => saveAs(blob, 'file.zip'))
                .catch((error) => {
                    console.log(error);
                });
        } else { alert(" number of documents cannot be <= 0"); }
    }

    return (
        <div className='generator-form'>
            <form>
                <Select
                    className="dropdown"
                    placeholder="Select invoice type"
                    value={invoiceType}
                    options={invoiceOptions}
                    onChange={(event) => handleChange(event, setInvoiceType)}
                    isMulti
                    isClearable
                />
                <br />
                <Select
                    className="dropdown"
                    placeholder="Select remittance type"
                    value={remittanceType}
                    options={remittanceOptions}
                    onChange={(event) => handleChange(event, setRemittanceType)}
                    isMulti
                    isClearable
                />
                <br />
                <Select
                    className="dropdown"
                    placeholder="Select finished file extension"
                    value={fileExtension}
                    options={extensionOptions}
                    onChange={(event) => handleChange(event, setFileExtension)}
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
                    value={docNumber}
                    onChange={(event) => handleChange(event, setDocNumber)}
                    min={0}
                />
                <br />

                <button type='button' onClick={downloadFile}>Download</button>
            </form>
        </div>
    );
}
