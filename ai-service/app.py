from flask import Flask, request, jsonify
from flask_cors import CORS

app = Flask(__name__)

CORS(app)

@app.route('/predict', methods=['GET'])
def predict():

    try:

        # Get values safely
        temperature = request.args.get('temperature')
        humidity = request.args.get('humidity')
        plant_type = request.args.get('plantType')

        # Validation
        if temperature is None or humidity is None:

            return jsonify({
                "error": "temperature and humidity are required"
            }), 400

        # Convert
        temperature = float(temperature)
        humidity = float(humidity)

        # Simple AI Logic
        watering_days = (
            7
            - (temperature / 10)
            + (humidity / 20)
        )

        # Prevent negative values
        watering_days = max(1, watering_days)

        return jsonify({
            "recommendedWateringDays": round(watering_days, 1),
            "plantType": plant_type
        })

    except Exception as e:

        print("ERROR:", e)

        return jsonify({
            "error": str(e)
        }), 500


if __name__ == '__main__':

    app.run(port=5000)