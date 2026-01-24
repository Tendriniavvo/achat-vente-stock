import axios from 'axios';

const API_URL = 'http://localhost:8080/api/roles';

class RoleService {
  async getAllRoles() {
    const response = await axios.get(API_URL);
    return response.data;
  }
}

export default new RoleService();
