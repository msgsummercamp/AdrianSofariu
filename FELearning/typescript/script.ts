import { DogApiResponse } from "./types";

async function fetchDogImage(): Promise<DogApiResponse> {
  const response: Response = await fetch(
    "https://dog.ceo/api/breeds/image/random"
  );
  if (!response.ok) {
    throw new Error("Network response was not ok");
  }
  return response.json();
}

async function displayImageOnClick(): Promise<void> {
  const loadDogButton =
    document.querySelector<HTMLButtonElement>(".load-dog-button");
  if (!loadDogButton) {
    throw new Error("Button not found in the DOM");
  }

  const dogImage = document.querySelector<HTMLImageElement>(".dog-image");
  if (!dogImage) {
    throw new Error("Dog image not found in the DOM");
  }

  const loadingText = document.querySelector<HTMLParagraphElement>(".loading");
  if (!loadingText) {
    throw new Error("Loading text not found in the DOM");
  }

  const errorMessage =
    document.querySelector<HTMLParagraphElement>(".error-message");
  if (!errorMessage) {
    alert("Error paragraph missing from DOM, cannot display error.");
    return;
  }

  errorMessage.textContent = "";
  errorMessage.classList.add("hidden");

  loadingText.classList.remove("hidden");
  dogImage.classList.add("hidden");

  try {
    fetchDogImage().then((response) => {
      dogImage.src = response.message;
      loadingText.classList.add("hidden");
      dogImage.classList.remove("hidden");
    });
  } catch (error) {
    dogImage.classList.add("hidden");
    errorMessage.textContent = "Failed to load dog image: " + error;
    errorMessage.classList.remove("hidden");
  }
}
