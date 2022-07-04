export const getDownloadFile = async (param) => {
    return fetch("http://localhost:8080/invoice",
        {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: param
        }).then((result) => {
        if (result.status !== 200) {
            throw new Error("Bad server response");
        } else {
            return result.blob();
        }
    })
}