package com.mvpmatch.vendingmachine.initializer;

import com.mvpmatch.vendingmachine.domain.Role;
import com.mvpmatch.vendingmachine.domain.VendingMachine;
import com.mvpmatch.vendingmachine.repository.RoleRepository;
import com.mvpmatch.vendingmachine.repository.VendingMachineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DatabaseInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private final List<Role.RoleType> roles = Arrays.asList(
            Role.RoleType.BUYER,
            Role.RoleType.SELLER
    );

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private VendingMachineRepository vendingMachineRepository;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        roles.forEach(role -> roleRepository.save(new Role(role)));

        VendingMachine vendingMachine = new VendingMachine();
        vendingMachineRepository.save(vendingMachine);
    }
}

