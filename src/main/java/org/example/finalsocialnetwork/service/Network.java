package org.example.finalsocialnetwork.service;

import org.example.finalsocialnetwork.domain.Friendship;
import org.example.finalsocialnetwork.domain.User;
import org.example.finalsocialnetwork.domain.Pair;
import org.example.finalsocialnetwork.repository.database.FriendshipRequestsDBRepository;
import org.example.finalsocialnetwork.util.Page;
import org.example.finalsocialnetwork.util.Pageable;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class Network {
    private final UserService userService;
    private final FriendshipService friendshipService;
   /// private final FriendshipRequestsDBRepository friendRequestRepository;
    private final Map<User, List<User>> graph;
    public Network(UserService userService, FriendshipService friendshipService) {
        this.userService = userService;
        this.friendshipService = friendshipService;
        ///this.friendRequestRepository = friendRequestRepository;
        this.graph = new HashMap<>();
    }

    private void addToGraph(User user1, User user2) {
        graph.computeIfAbsent(user1, k -> new ArrayList<>()).add(user2);
    }

    private void deleteFromGraph(User user1, User user2) {
        if(graph.containsKey(user1)) {
            graph.get(user1).remove(user2);
        }
        if(graph.containsKey(user2)) {
            graph.get(user2).remove(user1);
        }
    }

    public User addUser(String firstName, String lastName, String username) {
        return userService.addUser(firstName, lastName, username);
    }
    public Friendship addFriendship(Long iduser1, Long iduser2) {
        Optional<User> user1 = userService.find(iduser1);
        Optional<User> user2 = userService.find(iduser2);
        if(user1.isPresent() && user2.isPresent() ) {
            //daca implementez lista de prieteni in user, aici fac adaugarea
            this.addToGraph(user1.get(), user2.get());
            this.addToGraph(user2.get(), user1.get());
            return friendshipService.addFriendship(iduser1, iduser2,LocalDateTime.now());
        }
        else
            throw new IllegalArgumentException("ONE OF THE USERS DOES NOT EXIST");
    }
    public Friendship removeFriendship(Long iduser1, Long iduser2) {
        Optional<User> user1 = userService.find(iduser1);
        Optional<User> user2 = userService.find(iduser2);
        if(user1.isPresent() && user2.isPresent() ) {
            Pair<Long,Long> idfriendship = new Pair<>(iduser1, iduser2);
            Pair<Long,Long> idalternativ = new Pair<>(iduser2, iduser1);
            Optional<Friendship> f = friendshipService.find(idfriendship);
            Optional<Friendship> f2 = friendshipService.find(idalternativ);
            if(f.isPresent())
                friendshipService.delete(idfriendship);
            else
                if(f2.isPresent())
                    friendshipService.delete(idalternativ);
            if(f.isPresent() || f2.isPresent()) {
                this.deleteFromGraph(user1.get(),user2.get());
                return f.orElseGet(f2::get);
            }
        }
        return null;
    }
    public void deleteUser(Long iduser)
    {
        Optional<User> user = userService.find(iduser);
        if(user.isPresent())
        {
            /*
            Iterable<User> users = userService.getAll();
            for(User user2 : users){
                this.deleteFromGraph(user2,user.get());
            }*/

            userService.getAll().forEach(user2 -> deleteFromGraph(user2, user.get()));

            /*
            Iterable<Friendship> friendships = friendshipService.getAll();
            for(Friendship friendship : friendships){
                if(friendship.getUsername1().equals(username) || friendship.getUsername2().equals(username))
                    friendshipService.delete(friendship.getId());
            }*/

            friendshipService.getAll().forEach(friendship -> {
                if (friendship.getIduser1().equals(iduser) || friendship.getIduser2().equals(iduser)) {
                    friendshipService.delete(friendship.getId());
                }
            });


            this.userService.delete(iduser);
        }
    }
    public List<User> getFriends(Long iduser){
        Optional<User> user = userService.find(iduser);
        List<User> friends = new ArrayList<>();
        /*for(Friendship friendship : friendshipService.getAll()){
                if (friendship.getUsername1().equals(user.get().getId())) {
                    userService.find(friendship.getUsername2()).ifPresent(friends::add);
                }
                if (friendship.getUsername2().equals(user.get().getId())) {
                    userService.find(friendship.getUsername1()).ifPresent(friends::add);
                }

            }*/
        user.ifPresent(value -> friendshipService.getAll().forEach(friendship -> {
            if (friendship.getIduser1().equals(value.getId())) {
                userService.find(friendship.getIduser2()).ifPresent(friends::add);
            }
            if (friendship.getIduser2().equals(value.getId())) {
                userService.find(friendship.getIduser1()).ifPresent(friends::add);
            }
        }));
        return friends;
    }
    public List<User> BFS(User user, HashMap<User,Boolean> visited){
        Queue<User> queue = new LinkedList<>();
        List<User> users = new ArrayList<>();
        queue.add(user);
        visited.put(user, true);
        while(!queue.isEmpty()){
            User node = queue.poll();
            users.add(node);
            List<User> friends = getFriends(node.getId());
            /*for(User friend : friends){
                if(!visited.get(friend)){
                    visited.put(friend, true);
                    queue.add(friend);
                }
            }*/
            friends.forEach(friend -> {
                if (!visited.get(friend)) {
                    visited.put(friend, true);
                    queue.add(friend);
                }
            });

        }
        return users;
    }
    public int communitiesNumber(){
        AtomicInteger number = new AtomicInteger();
        HashMap<User,Boolean> visited = new HashMap<>();
        Iterable<User> users = userService.getAll();

        /*for(User user : users){
            visited.put(user, false);
        }
        for(User user : visited.keySet()){
            if(!visited.get(user)){
                this.BFS(user, visited);
                number++;
            }
        }*/

        users.forEach(user -> visited.put(user, false));

        visited.keySet().forEach(user -> {
            if (!visited.get(user)) {
                this.BFS(user, visited);
                number.getAndIncrement();
            }
        });

        return number.get();
    }
    public List<User> biggestComunity(){
        HashMap<User,Boolean> visited = new HashMap<>();
        Iterable<User> users = userService.getAll();
        AtomicReference<List<User>> biggest = new AtomicReference<>(new ArrayList<>());

        /*for(User user : users){
            visited.put(user, false);
        }
        for(User user : visited.keySet()){
            if(!visited.get(user)){
                List<User> possibleResult = BFS(user, visited);
                if(possibleResult.size() > biggest.size()){
                    biggest = possibleResult;
                }
            }
        }*/

        users.forEach(user -> visited.put(user, false));

        visited.keySet().forEach(user -> {
            if (!visited.get(user)) {
                List<User> possibleResult = BFS(user, visited);
                if (possibleResult.size() > biggest.get().size()) {
                    biggest.set(possibleResult);
                }
            }
        });

        return biggest.get();
    }

    public Iterable<Friendship> getAllFriendships(){
        return friendshipService.getAll();
    }

    public Iterable<User> getAllUsers(){
        return userService.getAll();
    }

    public Optional<User> getOneUser(Long iduser){
        return userService.find(iduser);
    }

    public Optional<Friendship> getOneFriendship(Long id1, Long id2){
        return friendshipService.find(new Pair<>(id1,id2));
    }

    public Optional<User> getUserByUsername(String username) {
        Iterable<User> it = userService.getAll();
        for(User u : it)
            if(u.getUsername().equals(username))
                return Optional.of(u);
        return Optional.empty();
    }

    public Page<User> findAllOnPage(Pageable pageable, Long iduser) {
        return friendshipService.findAllOnPage(pageable, iduser);
    }
}
