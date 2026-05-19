import pandas as pd
from sklearn.ensemble import RandomForestRegressor
import joblib

# Training dataset
data = {
    "temperature": [20, 25, 30, 35, 22, 28, 32, 18],
    "humidity": [80, 60, 40, 30, 70, 50, 35, 90],
    "plant_type": [0, 0, 0, 0, 1, 1, 1, 1],
    "watering_days": [10, 7, 5, 3, 9, 6, 4, 12]
}

# Create dataframe
df = pd.DataFrame(data)

# Features
X = df[["temperature", "humidity", "plant_type"]]

# Target
y = df["watering_days"]

# Create model
model = RandomForestRegressor(n_estimators=100)

# Train model
model.fit(X, y)

# Save model
joblib.dump(model, "watering_model.pkl")

print("AI model trained successfully!")