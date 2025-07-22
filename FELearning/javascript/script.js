async function fetchDogImage() {
  const response = await fetch("https://dog.ceo/api/breeds/image/random").catch(
    (error) => {
      alert("Error fetching dog image: " + error.message);
    }
  );
  return response.json();
}

function addDisplayDogImageListener() {
  document.querySelector(".load-dog-button")?.addEventListener("click", () => {
    const dogImage = document.querySelector(".dog-image");
    const loadingText = document.querySelector(".loading");

    if (!dogImage || !loadingText) {
      alert("DOM elements undefined");
      return;
    }

    loadingText.classList.remove("hidden");
    dogImage.classList.add("hidden");

    fetchDogImage().then((response) => {
      dogImage.src = response.message;
      loadingText.classList.add("hidden");
      dogImage.classList.remove("hidden");
    });
  });
}

document.addEventListener("DOMContentLoaded", () => {
  addDisplayDogImageListener();
});
