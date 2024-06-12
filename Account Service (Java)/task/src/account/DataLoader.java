package account;

import account.model.ROLE;
import account.model.entity.Group;
import account.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DataLoader {

    private final GroupRepository groupRepository;

    @Autowired
    public DataLoader(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
        createRoles();
    }

    private void createRoles() {
        try {
            groupRepository.save(new Group("administrator" ,ROLE.ADMINISTRATOR));
            groupRepository.save(new Group("user", ROLE.USER));
            groupRepository.save(new Group("accountant", ROLE.ACCOUNTANT));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}