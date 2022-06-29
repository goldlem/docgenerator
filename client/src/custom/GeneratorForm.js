import {getDownloadFile} from "./file";
import {saveAs} from 'file-saver'
import {Component} from 'react'

export class GeneratorForm extends Component {
    constructor(props) {
        super(props);

        this.state = {
            invoiceTemplate: 'invoice1',
            remittanceTemplate: '',
            documentNumber: 1,
            fileExtension: 'docx'
        }
        this.handleChange = this.handleChange.bind(this);

    }

    handleChange(event) {
        this.setState({
            [event.target.name]: event.target.value
        });
    }

    downloadFile = () => {
        getDownloadFile(JSON.stringify(this.state)).then(blob => saveAs(blob, 'file.zip'))
            .catch((error) => {
                console.log(error);
            });
    }

    render() {
        return (
            <form>
                <div>
                    <label htmlFor={"invoiceType"}>Choose a invoice template</label>
                    <select value={this.state.value} onChange={this.handleChange} name={"invoiceTemplate"}
                            id={"invoiceType"}>
                        <option value={"invoice1"}>Invoice1</option>
                        <option value={"invoice2"}>Invoice2</option>
                        <option value={"invoice3"}>Invoice3</option>
                        <option value={"invoice4"}>Invoice4</option>
                        <option value={"inv_with_rem1"}>InvoiceWithRemittance1</option>
                        <option value={"inv_with_rem2"}>InvoiceWithRemittance2</option>
                    </select>
                </div>
                <div>
                    <label htmlFor={"remittanceType"}>Choose a remittance template</label>
                    <select value={this.state.value} onChange={this.handleChange} name={"remittanceTemplate"}
                            id={"remittanceType"} placeholder={"remittance type"}>
                        <option value={""}>None</option>
                        <option value={"remittance1"}>Remittance1</option>
                        <option value={"remittance2"}>Remittance2</option>
                        <option value={"remittance3"}>Remittance3</option>
                        <option value={"remittance4"}>Remittance4</option>
                        <option value={"remittance5"}>Remittance5</option>
                        <option value={"remittance6"}>Remittance6</option>
                    </select>
                </div>
                <div>
                    <label>
                        Number of documents
                        <input type={"number"} min={1} value={this.state.value} defaultValue={1}
                               name={"documentNumber"} onChange={this.handleChange} required/>
                    </label>
                </div>
                <div>
                    <label htmlFor={"fileExtension"}>Choose a remittance template</label>
                    <select value={this.state.value} onChange={this.handleChange} name={"fileExtension"}
                            id={"fileExtension"}>
                        <option value={"docx"}>docx</option>
                        <option value={"pdf"}>pdf</option>
                        <option value={"html"}>html</option>
                    </select>
                </div>
                <div>
                    <button type='button' onClick={this.downloadFile}>Download</button>
                </div>
            </form>
        )
    }
}