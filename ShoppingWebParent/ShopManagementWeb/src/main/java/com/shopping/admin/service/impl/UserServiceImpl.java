package com.shopping.admin.service.impl;

import com.shopping.admin.repository.UserRepository;
import com.shopping.admin.service.UserService;
import com.shopping.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> listAll() {
        return userRepository.findAll(Sort.by("firstName").ascending());
    }

    @Override
    public Page<User> listByPage(int pageNum, String sortField, String sortDir,
                                    String emailSearch, String firstNameSearch, String lastNameSearch,
                                    int pageSize) {
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, sort);

        return userRepository.findAll(pageable, emailSearch, firstNameSearch, lastNameSearch);
    }

    @Override
    public User save(User userForm) {
        boolean isUpdatingUser = (userForm.getId() != null);
        if(isUpdatingUser) {
            User existingUser = userRepository.findById(userForm.getId()).get();
            if(userForm.getPassword().isEmpty()) {
                userForm.setPassword(existingUser.getPassword());
            }
        }
        return userRepository.save(userForm);
    }
    @Override
    public boolean isEmailUnique(Integer id, String email) {
        User userByEmail = userRepository.getUserByEmail(email);

        if(userByEmail == null) return true;

        boolean isCreatingNew = (id == null);

        if(isCreatingNew) { //create
            if(userByEmail != null) return false;
        } else { //edit
            if(userByEmail.getId() != id) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void delete(Integer id) {
        userRepository.deleteById(id);
    }

    @Override
    public User getUserById(Integer id) {
        return userRepository.findById(id).orElse(null);
    }
}
