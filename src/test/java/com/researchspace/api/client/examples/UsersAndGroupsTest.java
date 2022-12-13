package com.researchspace.api.client.examples;


import com.researchspace.api.client.ApiConnector;
import com.researchspace.api.clientmodel.GroupInfo;
import com.researchspace.api.clientmodel.GroupPost;
import com.researchspace.api.clientmodel.UserGroupInfo;
import com.researchspace.api.clientmodel.UserGroupPost;
import com.researchspace.api.clientmodel.UserPost;
import com.researchspace.api.clientmodel.UserRole;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UsersAndGroupsTest extends FixedIntervalTest {

    public static final String GROUP_NAME = "groupName";

    @Test
    void createUserAsNonPI() throws IOException, URISyntaxException {
        ApiConnector apiConnector = createApiConnector();
        UserPost userToCreate = createNonPIUserPost();
        UserGroupInfo createdUser = apiConnector.createUser(userToCreate, configuredApiKey);
        assertNotNull(createdUser);
        assertTrue(createdUser.getUsername().contains("userisuser"));
    }

    @Test
    void createUserAsPI() throws IOException, URISyntaxException {
        ApiConnector apiConnector = createApiConnector();
        UserPost userToCreate = createPIUserPost();
        UserGroupInfo createdUser = apiConnector.createUser(userToCreate, configuredApiKey);
        assertNotNull(createdUser);
        assertTrue(createdUser.getUsername().contains("userispi"));
    }

    @Test
    void createGroup() throws IOException, URISyntaxException {
        ApiConnector apiConnector = createApiConnector();
        UserPost userToCreatePI = createPIUserPost();
        UserGroupInfo createdPIUser = apiConnector.createUser(userToCreatePI, configuredApiKey);
        UserPost userToCreate1 = createNonPIUserPost();
        UserGroupInfo createdNonPIUser = apiConnector.createUser(userToCreate1, configuredApiKey);
        UserPost userToCreate2 = createNonPIUserPost();
        UserGroupInfo createdNonPIUser2 = apiConnector.createUser(userToCreate2, configuredApiKey);
        UserGroupPost groupPI = createPIUserGroupPost(userToCreatePI);
        UserGroupPost labAdmin = createLabAdminUserGroupPost(userToCreate1);
        UserGroupPost member = createMemberUserGroupPost(userToCreate2);
        GroupPost groupPost = createGroupPost(groupPI, List.of(labAdmin,member));
        GroupInfo createdGroup = apiConnector.createGroup(groupPost, configuredApiKey);
        assertTrue(createdGroup.getName().contains(GROUP_NAME));
        List <UserGroupInfo> members = createdGroup.getMembers();
        assertEquals(3, members.size());
        assertTrue(members.stream()
                .filter(amember -> amember.getUsername().contains("userispi")).collect(Collectors.toList()).size()>0);
        assertTrue(members.stream()
                .filter(amember -> amember.getRole().contains("PI")).collect(Collectors.toList()).size()>0);
        assertTrue(members.stream()
                .filter(amember -> amember.getUsername().contains("userisuser")).collect(Collectors.toList()).size()>1);
        assertTrue(members.stream()
                .filter(amember -> amember.getRole().contains("LAB_ADMIN")).collect(Collectors.toList()).size()>0);
        assertTrue(members.stream()
                .filter(amember -> amember.getRole().contains("USER")).collect(Collectors.toList()).size()>0);
    }

    private UserPost createPIUserPost() {
        return createUserPost(UserRole.ROLE_PI);
    }

    private UserPost createNonPIUserPost() {
        return createUserPost(UserRole.ROLE_USER);
    }

    private UserPost createUserPost(UserRole role) {
        //For a non sysadmin/admin user, they get created as USER or PI (other roles are assigned when join group)
        return UserPost.builder().username(role == UserRole.ROLE_USER ? Math.random()+"userisuser" : Math.random()+"userispi")
                .email("email"+Math.random()+"@email.com").password("password1234")
                .firstName("first").lastName("last")
                .role(role).build();
    }

    private UserGroupPost createPIUserGroupPost(UserPost up) {
        assertEquals(UserRole.ROLE_PI, up.getRole());
        return createUserGroupPost(UserGroupPost.RoleInGroup.PI, up);
    }

    private UserGroupPost createMemberUserGroupPost(UserPost up) {
        return createUserGroupPost(UserGroupPost.RoleInGroup.DEFAULT, up);
    }

    private UserGroupPost createLabAdminUserGroupPost(UserPost up) {
        return createUserGroupPost(UserGroupPost.RoleInGroup.RS_LAB_ADMIN, up);
    }

    private UserGroupPost createUserGroupPost(UserGroupPost.RoleInGroup role, UserPost up) {
        return UserGroupPost.builder().username(up.getUsername())
                .roleInGroup(role).build();
    }

    private GroupPost createGroupPost(UserGroupPost PI, List<UserGroupPost> members){
        assertEquals(UserGroupPost.RoleInGroup.PI, PI.getRoleInGroup());
        GroupPost.GroupPostBuilder builder = GroupPost.builder().displayName(Math.random()+ GROUP_NAME)
                        .user(PI);
        for(UserGroupPost ugp: members){
            builder.user(ugp);
        }
        return builder.build();
    }


}

