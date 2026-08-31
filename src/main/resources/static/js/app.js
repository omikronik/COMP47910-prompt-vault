"use strict";

document.querySelectorAll("[data-confirm]").forEach((form) => {
  form.addEventListener("submit", (event) => {
    if (!window.confirm(form.dataset.confirm)) event.preventDefault();
  });
});

document.querySelectorAll("[data-character-counter]").forEach((input) => {
  const counter = document.getElementById(input.dataset.characterCounter);
  if (!counter) return;
  const updateCounter = () => {
    counter.textContent = `${input.maxLength - input.value.length} remaining`;
  };
  input.addEventListener("input", updateCounter);
  updateCounter();
});

const chatForm = document.getElementById("chat-form");
const chatTextarea = document.getElementById("chat-textarea");
const sendButton = document.getElementById("send-btn");

if (chatForm && chatTextarea && sendButton) {
  window.scrollTo(0, document.body.scrollHeight);
  chatForm.addEventListener("submit", () => {
    chatTextarea.readOnly = true;
    chatTextarea.classList.add("is-submitting");
    sendButton.disabled = true;
    sendButton.textContent = "Sending…";
  });
  chatTextarea.addEventListener("keydown", (event) => {
    if (event.key === "Enter" && !event.shiftKey) {
      event.preventDefault();
      chatForm.requestSubmit();
    }
  });
}
