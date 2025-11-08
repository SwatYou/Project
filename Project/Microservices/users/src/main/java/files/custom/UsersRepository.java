package files.custom;

import org.springframework.data.repository.CrudRepository;

import files.user.UserEntity;

public interface UsersRepository extends CrudRepository<UserEntity, Long>{
    
}
