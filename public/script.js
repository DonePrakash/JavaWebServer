document.addEventListener("DOMContentLoaded", function () {
    const heading = document.querySelector("h1");

    heading.addEventListener("mouseover", function () {
        heading.style.color = "#007BFF";
        heading.textContent = "You hovered over me!";
    });

    heading.addEventListener("mouseout", function () {
        heading.style.color = "#4CAF50";
        heading.textContent = "Welcome to My Web Server!";
    });

    console.log("JavaScript is successfully loaded!");
});
