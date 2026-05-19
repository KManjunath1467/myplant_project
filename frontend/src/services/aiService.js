import axios from "axios";

const AI_API = "http://127.0.0.1:5000";

export const getWateringPrediction = async (
  temperature,
  humidity,
  plantType
) => {

  const response = await axios.get(
    `${AI_API}/predict`,
    {
      params: {
        temperature,
        humidity,
        plantType
      }
    }
  );

  console.log("FULL RESPONSE:", response);

  return response.data;
};