package retea.reteadesocializare.service;


import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import retea.reteadesocializare.domain.*;
import retea.reteadesocializare.domain.validators.MonthValidator;
import retea.reteadesocializare.domain.validators.ValidationException;
import retea.reteadesocializare.repository.Repository;
import retea.reteadesocializare.repository.db.EventsDbRepository;
import retea.reteadesocializare.repository.db.GroupsDbRepository;

import java.io.File;
import java.util.*;
import java.util.function.Predicate;

public class Service {
    private Repository<Long, User> userRepository;
    private Repository<Tuple<Long,Long>, Friendship> friendshipRepository;
    private Repository<Long, Message> messageRepository;
    private GroupsDbRepository groupsDbRepository;
    private EventsDbRepository eventsDbRepository;

    public Service(Repository<Long, User> userRepository, Repository<Tuple<Long,Long>, Friendship> friendshipRepository,Repository<Long, Message> messageRepository, GroupsDbRepository groupsDbRepository, EventsDbRepository eventsDbRepository) {

        this.userRepository = userRepository;
        this.friendshipRepository = friendshipRepository;
        this.messageRepository=messageRepository;
        this.groupsDbRepository=groupsDbRepository;
        this.eventsDbRepository = eventsDbRepository;
    }
    /**
     *
     * @param messageTask
     *         messageTask must be not null
     * @return null- if the given user is saved
     *         otherwise returns the user
     * @throws ValidationException
     *            if the user is not valid
     * @throws IllegalArgumentException
     *             if the given user is null.     *
     */
    public User addUser(User messageTask) throws ValidationException{
        User task = userRepository.save(messageTask);
        return task;
    }
    /**
     *
     * @return all users
     */
    public Iterable<User> getAllUsers(){

        return userRepository.findAll();
    }
    /**
     *  removes the user with the specified id
     * @param messageTask
     *      messageTask must be not null
     * @return the removed user or null if there is no user with the given id
     * @throws IllegalArgumentException
     *                   if the given messageTask is null.
     */
    public User deleteUser(Long messageTask) throws ServiceException{
        if(userRepository.findOne(messageTask) != null) {
            Iterable<Friendship> friendships = getAllFriendships();
            List<Friendship> friendshipList = new ArrayList();
            User user = findOneUser(messageTask);

            Iterable<Message> messages = messageRepository.findAll();
            for(Message message : messages)
                if(message.getFrom().equals(user) || message.getTo().contains(user))
                    throw new ServiceException("Can't delete user with messages!\n");

            for(Friendship friendship : friendships)
                friendshipList.add(friendship);

            for( Friendship friendship : friendshipList){
                if(user.getId().equals(friendship.getId().getLeft()) || user.getId().equals(friendship.getId().getRight()))
                    deleteFriendship(friendship.getId());
            }
            User task = userRepository.delete(messageTask);
            return task;
        }
        else throw new ServiceException("ID not found!");
    }
    /**
     *
     * @param messageTask -the id of the user to be returned
     *           messageTask must not be null
     * @return the user with the specified id
     *          or null - if there is no user with the given id
     * @throws IllegalArgumentException
     *                  if id is null.
     */
    public User findOneUser(Long messageTask){
        User user = userRepository.findOne(messageTask);
        return user;
    }
    /**
     *
     * @param messageTask
     *         messageTask must be not null
     * @return null- if the given friendship is saved
     *         otherwise returns the friendship
     * @throws ServiceException - if the friendship already exists
     */
    public Friendship addFriendship(Friendship messageTask) throws ServiceException{
        if(friendshipRepository.findOne(messageTask.getId()) == null) {
            Friendship task = friendshipRepository.save(messageTask);
            return task;
        }
        else throw new ServiceException("Friendship already exists!");
    }
    /**
     *  removes the friendship with the specified id
     * @param messageTask
     *      messageTask must be not null
     * @return the removed friendship or null if there is no friendship with the given id
     */
    public Friendship deleteFriendship (Tuple<Long,Long> messageTask){
        Friendship task = friendshipRepository.delete(messageTask);
        return task;
    }
    /**
     *
     * @return all friendships
     */
    public Iterable<Friendship> getAllFriendships(){

        Iterable<Friendship> allFriendships = friendshipRepository.findAll();
        Set<Friendship> friendships = new HashSet<>();
        for(Friendship friendship : allFriendships)
            if(friendship.getFriendshipStatus().equals("approved"))
                friendships.add(friendship);

        return friendships;

    }
    /**
     *
     * @param messageTask -the id of the friendship to be returned
     *           messageTask must not be null
     * @return the friendship with the specified id
     *          or null - if there is no friendship with the given id
     * @throws IllegalArgumentException
     *                  if id is null.
     */
    public Friendship findOneFriendship(Tuple<Long,Long> messageTask){
        Friendship friendship = friendshipRepository.findOne(messageTask);
        return friendship;
    }

    /**
     *
     * @return number of comunities
     */
    public int numberOfComunities(){
        Map<Long, List<Long>> graph = new HashMap<>();
        Map<Long, Integer> visited = new HashMap<>();
        Iterable<User> users = getAllUsers();
        for (User user : users){
            graph.put(user.getId(), new ArrayList<>());
            visited.put(user.getId(), 1);
        }
        Iterable<List<Long>> comunities = getAllComunities(graph, visited);
        int numberOfComunities =0 ;
        for (List<Long> comunityList : comunities){
            numberOfComunities++;
        }
        return numberOfComunities;
    }

    /**
     *
     * @return a list of user's IDs of the most sociable comunity
     */
    public List<Long> mostSociableComunity(){
        Map<Long, List<Long>> graph = new HashMap<>();
        Map<Long, Integer> visited = new HashMap<>();
        Iterable<User> users = getAllUsers();
        for (User user : users){
            graph.put(user.getId(), new ArrayList<>());
            visited.put(user.getId(), 1);
        }
        Iterable<List<Long>> comunities = getAllComunities(graph, visited);
        int maxSize=0;
        List<Long>result = new ArrayList<>();
        for (List<Long> comunityList : comunities){
            int size=0;
            for (Long memberId : comunityList){
                size++;
            }
            if (size>maxSize)
            {
                maxSize=size;
                result = new ArrayList<>(comunityList);
            }
        }
        return result;
    }

    /**
     *
     * @param id - user's id
     * @return a list of user's friends
     */
    public List<User> getUserFriends(Long id) {
        /*
        User user=findOneUser(id);
        Iterable<Friendship> friendships=getAllFriendships();
        List<Friendship> friendshipsList = new ArrayList<>();
        for(Friendship friendship: friendships){
            friendshipsList.add(friendship);
        }
        Predicate<Friendship> byFirstMember=x->x.getId().getLeft().equals(user.getId());
        Predicate<Friendship> bySecondMember=x->x.getId().getRight().equals(user.getId());
        Predicate<Friendship> filtered=byFirstMember.or(bySecondMember);

        List<User> friends=new ArrayList<>();
        friendshipsList.stream().filter(byFirstMember)
                .map(x->new Friendship(x.getId().getLeft(),x.getId().getRight()))
                .forEach(x-> friends.add(findOneUser(x.getId().getRight()))
                );

        friendshipsList.stream().filter(bySecondMember)
                .map(x->new Friendship(x.getId().getLeft(),x.getId().getRight()))
                .forEach(x-> friends.add(findOneUser(x.getId().getLeft()))
                );

        return friends;
    */return groupsDbRepository.findUserFriends(id);

    }
    /**
     *
     * @param id - user's id
     * @param month - the given month
     * @return a list of user's friends whose friendship was created in the given month
     * @throws ServiceException if user doesn't exists
     * @throws ValidationException if month is invalid
     */
    public List<User> getUserFriendsByGivenMonth(Long id, String month) throws ServiceException,ValidationException{
        if(userRepository.findOne(id) == null)
            throw new ServiceException("ID invalid");
        MonthValidator.validate(month);
        User user=findOneUser(id);
        Iterable<Friendship> friendships=getAllFriendships();
        List<Friendship> friendshipsList = new ArrayList<>();
        for(Friendship friendship: friendships){
            friendshipsList.add(friendship);
        }
        Predicate<Friendship> byFirstMember=x->x.getId().getLeft().equals(user.getId());
        Predicate<Friendship> bySecondMember=x->x.getId().getRight().equals(user.getId());
        Predicate<Friendship> byMonth=x->x.getDate().substring(5,7).equals(month);
        Predicate<Friendship> byFirstMemberByMonth = byFirstMember.and(byMonth);
        Predicate<Friendship> bySecondMemberByMonth = bySecondMember.and(byMonth);

        List<User> friends=new ArrayList<>();
        friendshipsList.stream().filter(byFirstMemberByMonth)
                .map(x->new Friendship(x.getId().getLeft(),x.getId().getRight(),x.getDate()))
                .forEach(x-> friends.add(findOneUser(x.getId().getRight()))
                );

        friendshipsList.stream().filter(bySecondMemberByMonth)
                .map(x->new Friendship(x.getId().getLeft(),x.getId().getRight(),x.getDate()))
                .forEach(x-> friends.add(findOneUser(x.getId().getLeft()))
                );

        return friends;


    }

    /**
     *
     * @param IdWriter - id of message sender
     * @param receivers - a string which contains message receivers
     * @param messageText - content of the message
     */

    public void writeMessage(Long IdWriter,String receivers,String messageText) {
        User from=userRepository.findOne(IdWriter);
        List<String> toAsString = Arrays.asList(receivers.split(";"));
        List<User> to = new ArrayList<User>();
        for (String userString : toAsString) {
            to.add(userRepository.findOne(Long.parseLong(userString)));
        }
        Message message=new Message(from,to,messageText);
        messageRepository.save(message);
    }

    /**
     *
     * @param Id1 - first user's id
     * @param Id2 - second user's id
     * @return a list which contains all messages between these users
     */
    public List<Message> seeMessagesBetweenTwoUsers(Long Id1,Long Id2){

        List<Message> searchedMessages=new ArrayList<>();
        Iterable<Message> messages=messageRepository.findAll();

        List<Message> messageList=new ArrayList<>();
        for(Message message:messages)
            messageList.add(message);
        Comparator<Message> compareById = (Message o1, Message o2) -> o1.getId().compareTo( o2.getId() );
        Collections.sort(messageList, compareById);


        for(Message message:messageList){
            if(message.getFrom().equals(userRepository.findOne(Id1)) && message.getTo().contains(userRepository.findOne(Id2))
             || message.getFrom().equals(userRepository.findOne(Id2)) && message.getTo().contains(userRepository.findOne(Id1)))
                searchedMessages.add(message);
        }

        return searchedMessages;
    }

    public List<Message> seeConversation(Long Id1,Long Id2) {
        return messageRepository.findConversation(Id1,Id2);
    }
    /**
     *
     * @param id - user's id
     * @return a list of unreplied messages
     */
    public List<Message> unrepliedMessages(Long id){
        List<Message> searchedMessages=new ArrayList<>();
        Iterable<Message> messages=messageRepository.findAll();
        for(Message message:messages) {
            if(message.getReply()==null) {
                if (message.getTo().contains(userRepository.findOne(id)))
                    searchedMessages.add(message);
            }
            else {
                boolean condition=true;
                for (Message replyMessage : message.getReply()) {
                    if (replyMessage.getFrom().equals(userRepository.findOne(id)))
                        condition = false;
                }
                if(message.getTo().contains(userRepository.findOne(id)) && condition)
                    searchedMessages.add(message);
            }
        }
        return searchedMessages;

    }

    /**
     *
     * @param IdWriter - sender's id
     * @param IdMessage - message's id
     * @param replyMessage - the response to the message
     * @throws ServiceException if message to respond doesn't exist
     */
    public void replyToOneUser(Long IdWriter,Long IdMessage,String replyMessage) throws ServiceException {
        if(messageRepository.findOne(IdMessage)==null)
            throw new ServiceException("Invalid message id!");
        User from=userRepository.findOne(IdWriter);

        Message initialMessage=messageRepository.findOne(IdMessage);

        List<User> to = new ArrayList<User>();
        to.add(initialMessage.getFrom());

        Message newMessage=new Message(from,to,replyMessage);
        messageRepository.save(newMessage);

        Iterable<Message> messages=messageRepository.findAll();
        List<Message> messageList=new ArrayList<>();
        for(Message message:messages)
            messageList.add(message);
        Comparator<Message> compareById = (Message o1, Message o2) -> o1.getId().compareTo( o2.getId() );
        Collections.sort(messageList, compareById);

        initialMessage.addNewReplyMessage(messageList.get(messageList.size()-1));
        messageRepository.update(initialMessage);


    }

    /**
     *
     * @param id - user's id
     * @throws ServiceException if the user's id doesn't exist
     */
    public void checkUserExistence(Long id) throws ServiceException {
        if(userRepository.findOne(id)==null)
            throw new ServiceException("Id does not exist");
    }

    public Long findOneUser(String username, String password) throws ServiceException{
        Iterable<User> users=new ArrayList<>();
        users=getAllUsers();

        for(User user:users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password))
                return user.getId();
            else if(user.getUsername().equals(username) && !user.getPassword().equals(password))
                throw new ServiceException("Incorrect password");
        }

        throw new ServiceException("Account doesn't exist");
    }

    /**
     *
     * @param id - message's id
     * @return message
     */
    public Message findOneMessage(Long id){
        return messageRepository.findOne(id);
    }

    /**
     *
     * @param idFrom - sender's id
     * @param idTo - receiver's id
     * @throws ServiceException - if there is already a friend request between these users
     */
    public void sendFriendRequest(Long idFrom, Long idTo) throws ServiceException {
        boolean verify = false;
        Long sender1 = idFrom;
        if(idFrom > idTo){
            Long swap = idFrom;
            idFrom = idTo;
            idTo = swap;
            verify = true;
        }
        Friendship friendship1 = friendshipRepository.findOne(new Tuple<Long, Long> (idFrom, idTo));
        if(friendship1 != null) {
            Long sender = friendship1.getSender();
            if (friendship1.getFriendshipStatus().equals("pending"))
                throw new ServiceException("You have already sent a friend request!");
            if (friendship1.getFriendshipStatus().equals("approved"))
                throw new ServiceException("You are already friends!");
            if (friendship1.getFriendshipStatus().equals("rejected") && verify == false && sender.equals(idFrom) || friendship1.getFriendshipStatus().equals("rejected") && verify == true && sender.equals(idTo))
                throw new ServiceException("This user rejected your friend request!");
            if (friendship1.getFriendshipStatus().equals("rejected") && verify == false && sender.equals(idTo) || friendship1.getFriendshipStatus().equals("rejected") && verify == true && sender.equals(idFrom))
                throw new ServiceException("You rejected his friend request!\nYou can't send a friend request anymore!");

        }
        Friendship friendship = new Friendship("pending", idFrom, idTo, sender1);
        friendshipRepository.save(friendship);
    }

    /**
     *
     * @param idFrom - id of the user who sent the friend request
     * @param idTo - id of the user to respond the friend request
     * @param response - friend request's response(approved, rejected)
     */
    public void responseToFriendRequest(Long idFrom, Long idTo, String response) {
        Long sender = idFrom;
        if(idFrom > idTo){
            Long swap = idFrom;
            idFrom = idTo;
            idTo = swap;
        }
        Friendship newFriendship = new Friendship(response, idFrom, idTo, sender);
        friendshipRepository.update(newFriendship);
    }

    /**
     *
     * @param id - user's id
     * @return a list of user's pending friend requests
     */
    public List<User> pendingFriendships(Long id) {
        /*List<Friendship> pendingFriendships = new ArrayList<>();
        Iterable<Friendship> friendships = friendshipRepository.findAll();
        for (Friendship friendship : friendships) {
            if( ((friendship.getId().getLeft().equals(id) && !friendship.getId().getLeft().equals(friendship.getSender()) )
                    || (friendship.getId().getRight().equals(id) && !friendship.getId().getRight().equals(friendship.getSender())))
                    && friendship.getFriendshipStatus().equals("pending") )
                pendingFriendships.add(friendship);
        }
        return pendingFriendships;*/

        return groupsDbRepository.findUserRequests(id);
    }
    public Iterable<User> findAllUsersStartsWith(String text){

        return userRepository.findAllUsersStartsWith(text);
    }

    private void through(Long i, List<Long> entries, Map<Long, Integer> visited,  Map<Long, List<Long>>graph){
        visited.put(i, 2);
        entries.add(i);
        List<Long>neighbours = graph.get(i);
        for (Long k : neighbours)
        {
            if (visited.get(k) == 1)
                through(k, entries, visited, graph);
        }
    }

    public Iterable<List<Long>> getAllComunities(Map<Long, List<Long>> graph, Map<Long, Integer> visited)
    {
        Iterable<Friendship> friendships = getAllFriendships();
        //Retine 1 pentru entries care nu au fost vizitate in dfs, si 2 pentru cele vizitate
        List<List<Long>> result = new ArrayList<>();
        //Pentru fiecare nod, retine vecii lui
        // Map<Long, List<Long>>graph = new HashMap<Long, List<Long>>();
        for (Friendship friendship : friendships) {
            List<Long> friend1 = graph.get(friendship.getId().getLeft());
            List<Long> friend2 = graph.get(friendship.getId().getRight());
            if (friend1==null)
                friend1 = new ArrayList<>();
            if (friend2==null)
                friend2 = new ArrayList<>();
            friend1.add(friendship.getId().getRight());
            friend2.add(friendship.getId().getLeft());

            graph.put(friendship.getId().getLeft(), friend1);
            graph.put(friendship.getId().getRight(), friend2);
            visited.put(friendship.getId().getLeft(), 1);
            visited.put(friendship.getId().getRight(), 1);
        }

        for (Long k : visited.keySet()){
            Integer v = visited.get(k);
            if (visited.get(k) == 1)
            {
                List<Long> newList = new ArrayList<>();
                through(k, newList, visited, graph);
                result.add(newList);
            }
        }


        return result;
    }

    public List<User> getUserSentFriendRequests(Long id) {
        List<User> usersSentFriendRequests = new ArrayList<>();
        Iterable<Friendship> friendships = friendshipRepository.findAll();
        for (Friendship friendship : friendships) {
            if (((friendship.getId().getLeft().equals(id) && friendship.getId().getLeft().equals(friendship.getSender()))
                    && friendship.getFriendshipStatus().equals("pending"))) {
                usersSentFriendRequests.add(userRepository.findOne(friendship.getId().getRight()));
            }

            if ((friendship.getId().getRight().equals(id) && friendship.getId().getRight().equals(friendship.getSender())) && friendship.getFriendshipStatus().equals("pending"))
                usersSentFriendRequests.add(userRepository.findOne(friendship.getId().getLeft()));


        }
        return usersSentFriendRequests;
    }

    public List<Group> getUserGroups(Long id){
        return groupsDbRepository.findGroups(id);
    }

    public void saveGroup(String name, List<User> users, User creatorUser, File image){
        groupsDbRepository.saveGroup(name,image);
        Group group= groupsDbRepository.getLastCreatedGroup();
        for(User user:users){
            groupsDbRepository.saveUserToGroup(user,group);
        }
        groupsDbRepository.saveUserToGroup(creatorUser,group);
    }

    public User findUser(Long id){
        return userRepository.findOne(id);
    }

    public List<Message> seeGroupConversation(Long groupId){
        return groupsDbRepository.findMessages(groupId);

    }

    public void saveMessageToGroup(Long idSender,String messageText,Long idGroup){
        List<User> to=new ArrayList<>();
        Message message=new Message(userRepository.findOne(idSender),to,messageText);
        messageRepository.save(message);
        Message lastMessage=groupsDbRepository.getLastCreatedMessage();
        groupsDbRepository.saveMessageToGroup(lastMessage,idGroup);

    }

    public void saveAvatar(Long id, File image){
        groupsDbRepository.saveAvatar(id,image);
    }

    public Image loadAvatar(Long id){
        return groupsDbRepository.findAvatar(id);
    }

    public Image loadGroupAvatar(Long id){
        return groupsDbRepository.loadGroupAvatar(id);
    }

    public User getLastCreatedUser(){
        return groupsDbRepository.getLastCreatedUser();
    }

    public void deleteAvatar(Long id){
        groupsDbRepository.deleteAvatar(id);
    }

    public Group findGroup(Long id){return groupsDbRepository.findOne(id);}

    public void setMessagesAsSeen(Long id1,Long id2){groupsDbRepository.setMessagesAsSeen(id1,id2);}

    public List<Message> findUnseenMessages(Long id){return groupsDbRepository.findUnseenMessages(id);}

    public void saveEvent(String name, Long creatorUser, File image, String date, String location, String description){
        eventsDbRepository.saveEvent(name, date, location, description, creatorUser, image);
        Event event= eventsDbRepository.getLastCreatedEvent();
        eventsDbRepository.saveUserToEvent(creatorUser, event);
    }

    public void addUserToEvent(Long event_id, Long user){
        Event event = eventsDbRepository.findOne(event_id);
        eventsDbRepository.saveUserToEvent(user, event);
    }

    public List<User> findUsersEvent(Long id_event){
        return eventsDbRepository.findUsers(id_event);
    }

    public List<Event> findEvents(Long user){
        return eventsDbRepository.findEvents(user);
    }
    public Image loadEventAvatar(Long event){
        return eventsDbRepository.loadEventAvatar(event);
    }

    public Iterable<Event> findEventsStartsWith(String text){
        return eventsDbRepository.findAllEventsStartsWith(text);
    }

    public Event findEvent(Long id){
        return eventsDbRepository.findOne(id);
    }

    public boolean checkUserEvent(Long idUser, Long idEvent){
        return eventsDbRepository.checkUserEvent(idUser, idEvent);
    }
    public boolean checkUserHasNotifications(Long idUser, Long idEvent,Long minutesLeft){
        return eventsDbRepository.checkUserHasNotifications(idUser,idEvent,minutesLeft);
    }

    public void deleteUserFromEvent(Long idUser, Long idEvent){
        eventsDbRepository.deleteUserFromEvent(idUser, idEvent);
    }

    public Event getLastCreatedEvent(){

        return eventsDbRepository.getLastCreatedEvent();
    }

    public boolean checkUserEventNotifications(Long idUser, Long idEvent){
        return eventsDbRepository.checkUserEventNotifications(idUser, idEvent);
    }

    public void setNotificationsOn(Long idUser, Long idEvent){
        eventsDbRepository.setNotificationOn(idUser, idEvent);
    }

    public void setNotificationsOff(Long idUser, Long idEvent){
        eventsDbRepository.setNotificationOff(idUser, idEvent);
    }

    public int getLastNotificationSeenFromUser(Long userId, Long eventId){
        return eventsDbRepository.getLastNotificationSeenFromUser(userId, eventId);
    }

    public void setLastNotificationSeenFromUser(Long userId, Long eventId, int notification){
        eventsDbRepository.setLastNotificationSeenFromUser(userId,eventId,notification);
    }
}

