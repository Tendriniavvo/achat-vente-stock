import axios from 'axios';

const API_URL = '/api/roles';

class RoleService {
  async getAllRoles() {
    const response = await axios.get(API_URL);
    return response.data;
  }
}

export default new RoleService();
