"use strict";
async function fetchDogImage() {
    const response = await fetch("https://dog.ceo/api/breeds/image/random");
    if (!response.ok) {
        throw new Error("Network response was not ok");
    }
    return response.json();
}
document.addEventListener("DOMContentLoaded", () => {
    const loadDogButton = document.querySelector(".load-dog-button");
    const dogImage = document.querySelector(".dog-image");
    const loadingText = document.querySelector(".loading");
    const errorMessage = document.querySelector(".error-message");
    function displayImageOnClick() {
        if (!loadDogButton || !dogImage || !loadingText || !errorMessage) {
            alert("Missing DOM elements!");
            return;
        }
        const localDogImage = dogImage;
        const localErrorMessage = errorMessage;
        localErrorMessage.textContent = "";
        localErrorMessage.classList.add("hidden");
        loadingText.classList.remove("hidden");
        localDogImage.classList.add("hidden");
        fetchDogImage()
            .then((response) => {
            localDogImage.src = response.message;
            localDogImage.classList.remove("hidden");
        })
            .catch((error) => {
            localDogImage.classList.add("hidden");
            localErrorMessage.textContent = "Failed to load dog image: " + error;
            localErrorMessage.classList.remove("hidden");
        })
            .finally(() => {
            loadingText === null || loadingText === void 0 ? void 0 : loadingText.classList.add("hidden");
        });
    }
});
