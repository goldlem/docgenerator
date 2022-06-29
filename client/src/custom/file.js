export const getDownloadFile = async (param) => {
    return fetch("http://192.168.1.6:8080/invoice",
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