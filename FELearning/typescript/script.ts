type DogApiResponse = {
  message: string;
  status: "success" | "error";
};

let loadDogButton: HTMLButtonElement | null;
let dogImage: HTMLImageElement | null;
let loadingText: HTMLParagraphElement | null;
let errorMessage: HTMLParagraphElement | null;

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
    .then((response: DogApiResponse) => {
      localDogImage.src = response.message;
      localDogImage.classList.remove("hidden");
    })
    .catch((error: Error) => {
      localDogImage.classList.add("hidden");
      localErrorMessage.textContent = "Failed to load dog image: " + error;
      localErrorMessage.classList.remove("hidden");
    })
    .finally(() => {
      loadingText?.classList.add("hidden");
    });
}

document.addEventListener("DOMContentLoaded", () => {
  loadDogButton = document.querySelector<HTMLButtonElement>(".load-dog-button");
  dogImage = document.querySelector<HTMLImageElement>(".dog-image");
  loadingText = document.querySelector<HTMLParagraphElement>(".loading");
  errorMessage = document.querySelector<HTMLParagraphElement>(".error-message");
});
