package ces.riccico.repository;

import org.springframework.data.repository.CrudRepository;

import ces.riccico.models.Accounts;

public interface AccountRepository extends CrudRepository<Accounts, String>  {

}
