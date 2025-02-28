import requests

# URL вашего Flask API
BASE_URL = "http://127.0.0.1:5000/api/products"

# Получение всех продуктов
def get_all_products():
    response = requests.get(BASE_URL)
    if response.status_code == 200:
        products = response.json()  # Преобразуем ответ в JSON
        return products
    else:
        print(f"Ошибка при получении всех продуктов: {response.status_code}")
        return []

# Получение конкретного продукта по ID
def get_product_by_id(product_id):
    url = f"{BASE_URL}/{product_id}"
    response = requests.get(url)
    if response.status_code == 200:
        product = response.json()  # Преобразуем ответ в JSON
        return product
    else:
        print(f"Ошибка при получении продукта с ID {product_id}: {response.status_code}")
        return None

# Пример использования

# Получаем все продукты
products = get_all_products()
print("Все продукты:")
print(products)

# Получаем продукт с ID 1
product_id = 1
product = get_product_by_id(product_id)
if product:
    print(f"Продукт с ID {product_id}:")
    print(product)
