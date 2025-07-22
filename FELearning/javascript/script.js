async function fetchDogImage() {
  try {
    const response = await fetch("https://dog.ceo/api/breeds/image/random");
    const data = await response.json();
    console.log(data);
    console.log(data.message);
    return data.message;
  } catch (error) {
    alert("Error fetching dog image: " + error.message);
    console.error("Error fetching dog image:", error);
  }
}

function addDisplayDogImageListener() {
  document.getElementById("loadDog").addEventListener("click", async () => {
    const dogContainer = document.getElementById("dogContainer");
    const loading = document.getElementById("loading");
    const dogImage = document.getElementById("dogImage");

    loading.style.display = "block";
    dogImage.style.display = "none";

    const dogUrl = await fetchDogImage();
    dogImage.src = dogUrl;

    loading.style.display = "none";
    dogImage.style.display = "block";
  });
}

document.addEventListener("DOMContentLoaded", () => {
  addDisplayDogImageListener();
});
