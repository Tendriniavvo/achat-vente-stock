import axios from 'axios';

const API_URL = '/api/departements';

class DepartementService {
  async getAllDepartements() {
    const response = await axios.get(API_URL);
    return response.data;
  }
}

export default new DepartementService();
