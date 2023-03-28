package com.shopping.admin.service;

import com.shopping.common.entity.User;
import org.springframework.data.domain.Page;

import java.util.List;


public interface UserService {
    public List<User> listAll();
    public Page<User> listByPage(int pageNum, String sortField, String sortDir,
                                    String emailSearch, String firstNameSearch, String lastNameSearch,
                                    int pageSize);

    public User save(User user);

    public boolean isEmailUnique(Integer id, String email);
    public void delete(Integer id);

    User getUserById(Integer id);
}
