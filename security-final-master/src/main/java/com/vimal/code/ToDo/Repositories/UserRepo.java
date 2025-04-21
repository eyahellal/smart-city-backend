package com.vimal.code.ToDo.Repositories;

import com.vimal.code.ToDo.models.Role;
import com.vimal.code.ToDo.models.UserEnitiy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepo extends JpaRepository<UserEnitiy,Long> {

    public Optional<UserEnitiy> findByEmail(String email);
    List<UserEnitiy> findByRole(Role role);

}
