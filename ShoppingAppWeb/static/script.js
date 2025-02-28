document.addEventListener("DOMContentLoaded", () => {
    const productsTableBody = document.querySelector("#products-table tbody");
    const addProductForm = document.querySelector("#add-product-form");
    const unitSelect = document.querySelector("#unit");
    const typeInput = document.querySelector("#type");
    const volumeInput = document.querySelector("#volume");

    // Показывать поле "Цена за объём", если выбрана не "шт."
    unitSelect.addEventListener("change", () => {
        if (unitSelect.value === "шт.") {
            typeInput.value = "штучный";
            volumeInput.style.display = "none";
            volumeInput.volume = 1;
        } else {
            typeInput.value = "на развес";
            volumeInput.style.display = "block";
        }
    });

    // Загрузка данных продуктов
    function loadProducts() {
        fetch("/api/Product")
            .then(response => response.json())
            .then(data => {
                productsTableBody.innerHTML = "";
                data.forEach(product => {
                    const row = document.createElement("tr");
                    row.innerHTML = `
                        <td>${product.id}</td>
                        <td>${product.name}</td>
                        <td>${product.category}</td>
                        <td>${product.price + " руб. за " + product.volume + " " + product.unit}</td>
                        <td>
                            <button onclick="deleteProduct(${product.id})">Удалить</button>
                        </td>
                    `;
                    productsTableBody.appendChild(row);
                });
            });
    }

    // Добавление нового продукта
    addProductForm.addEventListener("submit", (event) => {
        event.preventDefault();
        const name = document.querySelector("#name").value;
        const price = document.querySelector("#price").value;
        const category = document.querySelector("#category").value;
        const unit = unitSelect.value;
        const type = typeInput.value;
        const volumePrice = type === "на развес" ? document.querySelector("#volume").value : 1;

        fetch("/api/Product", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ name, price, category, type, unit, volume: volumePrice })
        }).then(response => {
            if (response.ok) {
                loadProducts();
                addProductForm.reset();
                volumeInput.style.display = "none";  // Скрываем поле после отправки
            } else {
                alert("Ошибка добавления продукта.");
            }
        });
    });

    // Удаление продукта
    window.deleteProduct = (id) => {
        fetch(`/api/SingleProduct/${id}`, { method: "DELETE" })
            .then(response => {
                if (response.ok) {
                    loadProducts();
                } else {
                    alert("Ошибка удаления продукта.");
                }
            });
    };

    // Загрузка данных при загрузке страницы
    loadProducts();
});
