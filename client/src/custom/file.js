export const getDownloadFile = async (url,param) => {
    return fetch(url,
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