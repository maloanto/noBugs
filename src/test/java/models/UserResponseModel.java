package models;

import java.util.List;

public class UserResponseModel {

        private Integer id;
        private String username;
        private String password;
        private String name;
        private String role;
        private List<Object> accounts; // или создай класс Account если знаешь структуру

        public UserResponseModel() {
        }


        // Геттеры и сеттеры
        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public List<Object> getAccounts() {
            return accounts;
        }

        public void setAccounts(List<Object> accounts) {
            this.accounts = accounts;
        }

        @Override
        public String toString() {
            return "User{" +
                    "id=" + id +
                    ", username='" + username + '\'' +
                    ", password='" + password + '\'' +
                    ", name='" + name + '\'' +
                    ", role='" + role + '\'' +
                    ", accounts=" + accounts +
                    '}';
        }
}
