from flask import Flask, request, jsonify, render_template
from flask_restful import Api, Resource
import sqlite3

app = Flask(__name__)
api = Api(app)

import sqlite3

def get_db_connection():
    conn = sqlite3.connect('shopping.db')
    conn.row_factory = sqlite3.Row
    return conn

def initialize_db():
    with get_db_connection() as db:
        db.execute('''
            CREATE TABLE IF NOT EXISTS products (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                price REAL NOT NULL,
                category TEXT NOT NULL,
                type TEXT NOT NULL,
                unit TEXT NOT NULL,
                volume UNSIGNED INTEGER   
            )
        ''')
        db.commit()
    print("База данных инициализирована.")

class Product(Resource):
    """Работа с полным списком продуктов"""
    
    def get(self):
        """Получение всех товаров"""
        with get_db_connection() as db:
            products = db.execute('SELECT * FROM products').fetchall()
            return jsonify([dict(row) for row in products])

    def post(self):
        """Добавление нового товара"""
        data = request.get_json()
        name = data.get('name')
        price = data.get('price')
        category = data.get('category')
        product_type = data.get('type')
        unit = data.get('unit')
        volume = data.get('volume')

        if not all([name, price, category, product_type, unit]):
            return {"error": "Отсутствуют обязательные поля (name, price, category, type, unit)"}, 400

        with get_db_connection() as db:
            db.execute(
                'INSERT INTO products (name, price, category, type, unit, volume) VALUES (?, ?, ?, ?, ?, ?)',
                (name, price, category, product_type, unit, volume)
            )
            db.commit()

        return {"message": "Продукт успешно создан"}, 201

class SingleProduct(Resource):
    """Работа с отдельным товаром по ID"""

    def get(self, product_id):
        """Получение информации о товаре по ID"""
        with get_db_connection() as db:
            product = db.execute('SELECT * FROM products WHERE id = ?', (product_id,)).fetchone()
            if product:
                return jsonify(dict(product))
            return {"error": "Продукт не найден"}, 404

    def put(self, product_id):
        """Обновление информации о товаре"""
        data = request.get_json()
        name = data.get('name')
        price = data.get('price')
        category = data.get('category')
        product_type = data.get('type')
        unit = data.get('unit')
        volume = data.get('volume')

        if not all([name, price, category, product_type, unit]):
            return {"error": "Отсутствуют обязательные поля (name, price, category, type, unit)"}, 400

        with get_db_connection() as db:
            updated = db.execute('''
                UPDATE products
                SET name = ?, price = ?, category = ?, type = ?, unit = ?, volume = ?
                WHERE id = ?
            ''', (name, price, category, product_type, unit, volume, product_id)).rowcount
            db.commit()

        if updated:
            return {"message": "Продукт успешно обновлён"}, 200
        return {"error": "Продукт не найден"}, 404

    def delete(self, product_id):
        """Удаление товара по ID"""
        with get_db_connection() as db:
            deleted = db.execute('DELETE FROM products WHERE id = ?', (product_id,)).rowcount
            db.commit()

        if deleted:
            return {"message": "Продукт успешно удалён"}, 200
        return {"error": "Продукт не найден"}, 404

class ProductSearch(Resource):
    """Поиск товаров по названию и фильтрам"""

    def get(self):
        """Метод поиска товаров по части названия и фильтрам"""
        name = request.args.get('name', '') 
        category = request.args.get('category', None)  

        query = "SELECT * FROM products WHERE name LIKE ?"
        params = [f"%{name}%"]

        if category:
            query += " AND category = ?"
            params.append(category)

        with get_db_connection() as db:
            products = db.execute(query, params).fetchall()
            return jsonify([dict(row) for row in products])

# Регистрация маршрутов API
api.add_resource(Product, "/api/Product")
api.add_resource(SingleProduct, "/api/SingleProduct/<int:product_id>")
api.add_resource(ProductSearch, "/api/ProductSearch")

@app.route("/")
def index():
    return render_template("index.html")

initialize_db()

if __name__ == "__main__":
    app.run(debug=True, port=5000, host="127.0.0.1")
