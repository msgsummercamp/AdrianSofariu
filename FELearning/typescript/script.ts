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

function displayImageOnClick(): void {
  const loadDogButton =
    document.querySelector<HTMLButtonElement>(".load-dog-button");
  if (!loadDogButton) {
    alert("Missing button in the DOM");
    return;
  }

  const dogImage = document.querySelector<HTMLImageElement>(".dog-image");
  if (!dogImage) {
    alert("Dog image not found in the DOM");
    return;
  }

  const loadingText = document.querySelector<HTMLParagraphElement>(".loading");

  const errorMessage =
    document.querySelector<HTMLParagraphElement>(".error-message");
  if (!errorMessage) {
    alert("Error paragraph missing from DOM, cannot display error.");
    return;
  }

  errorMessage.textContent = "";
  errorMessage.classList.add("hidden");

  loadingText?.classList.remove("hidden");
  dogImage.classList.add("hidden");

  fetchDogImage()
    .then((response: DogApiResponse) => {
      dogImage.src = response.message;
      dogImage.classList.remove("hidden");
    })
    .catch((error: Error) => {
      dogImage.classList.add("hidden");
      errorMessage.textContent = "Failed to load dog image: " + error;
      errorMessage.classList.remove("hidden");
    })
    .finally(() => {
      loadingText?.classList.add("hidden");
    });
}

document.addEventListener("DOMContentLoaded", () => {
  const loadDogButton =
    document.querySelector<HTMLButtonElement>(".load-dog-button");
  if (loadDogButton) {
    loadDogButton.addEventListener("click", displayImageOnClick);
  } else {
    alert("Load Dog button not found in the DOM");
  }
});
