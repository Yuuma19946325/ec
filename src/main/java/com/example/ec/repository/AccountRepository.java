package com.example.ec.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.ec.entity.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

	@Query(value = "select * from account where mail_address = :mail_Address AND password = :password", nativeQuery = true) // SQL
	Account findByMailAddressAndPassword(@Param("mail_Address") String mailAddress, @Param("password") String password);

	@Query(value = "select count(*) from account where mail_address = :mail_Address", nativeQuery = true) // SQL
	int findByMailAddress(@Param("mail_Address") String mailAddress);
}
