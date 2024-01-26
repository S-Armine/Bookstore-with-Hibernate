package org.bookstore;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.sql.Connection;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class ConsoleApp {
    private Scanner scanner = new Scanner(System.in);

    /**
     * This method is used to start the application.
     */
    public void run() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            actions: while (true) {
                System.out.println(menu());
                int choiceValue;
                int firstValueInMenu = 0;
                int lastValueInMenu = 9;
                if (scanner.hasNextInt()) {
                    choiceValue = scanner.nextInt();
                    scanner.nextLine();
                } else {
                    System.out.println("Invalid input. Please enter a number between 0 and 9.");
                    scanner.nextLine();
                    continue actions;
                }
                if (choiceValue < firstValueInMenu || choiceValue > lastValueInMenu){
                    System.out.println("Invalid input. Please enter a number between " +
                            firstValueInMenu + " and " + lastValueInMenu);
                    continue actions;
                }
                ActionType action = ActionType.fromValue(choiceValue);
                switch (action) {
                    case UPDATE_BOOK_DETAILS -> updateBookDetails(session);
                    case LIST_BOOKS_BY_GENRE -> listBooksByGenre(session);
                    case LIST_BOOKS_BY_AUTHOR -> listBooksByAuthor(session);
                    case UPDATE_CUSTOMERS_INFO -> updateCustomerInformation(session);
                    case CUSTOMERS_PURCHASE_HISTORY -> customerHistory(session);
                    case REVENUE_BY_GENRE -> calculateRevenue(session);
                    case PROCESS_NEW_SALE -> processNewSale(session);
                    case SOLD_BOOK_REPORT -> getBookReport(session);
                    case REVENUE_BY_GENRE_REPORT -> getGenreRevenues(session);
                    case EXIT -> {
                        break actions;
                    }
                }
                System.out.println();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            HibernateUtil.shutdown();
        }
    }

    /**
     * Method for getting menu of actions that are available in app
     * @return String representation of actions
     */
    public String menu() {
        StringBuilder stringBuilder = new StringBuilder();
        return stringBuilder.append("Choose action you want to execute from list bellow.\n")
                .append("1: Update book details.\n")
                .append("2: List books by genre.\n")
                .append("3: List books by author.\n")
                .append("4: Update customer information.\n")
                .append("5: View a customer’s purchase history.\n")
                .append("6: Calculate total revenue by genre.\n")
                .append("7: Process new sale.\n")
                .append("8: Generate a report of all books sold.\n")
                .append("9: Generate a report of revenue of each genre.\n")
                .append("0: Exit.")
                .toString();
    }

    /**
     * THis method is used for updating book's details in database
     * @param session The Hibernate session
     */
    private void updateBookDetails(Session session) {
        Transaction transaction = session.beginTransaction();
        Book book = getBookByID(session);
        if (book == null) {
            System.out.println("There is no book with given identifier.");
            transaction.rollback();
            return;
        }
        updating: while (true) {
            System.out.println("Choose column you want to update");
            System.out.println("1: title");
            System.out.println("2: author");
            System.out.println("3: genre");
            System.out.println("4: price");
            System.out.println("5: quantity in stock");
            System.out.println("0: exit");
            String choice = scanner.next();
            switch (choice) {
                case "1" -> {
                    System.out.println("Input new title.");
                    String newTitle = scanner.next();
                    book.setTitle(newTitle);
                }
                case "2" -> {
                    System.out.println("Input new author.");
                    String newAuthor = scanner.next();
                    book.setAuthor(newAuthor);
                }
                case "3" -> {
                    System.out.println("Input new genre.");
                    String newGenre = scanner.next();
                    book.setGenre(newGenre);
                }
                case "4" -> {
                    while (true) {
                        System.out.println("Input new price.");
                        if (scanner.hasNextFloat()) {
                            float newPrice = scanner.nextFloat();
                            if (newPrice > 0) {
                                book.setPrice(newPrice);
                                break;
                            }
                        }
                        System.out.println("Price should be positive floating point number.");
                    }
                }
                case "5" -> {
                    while (true) {
                        System.out.println("Input quantity in stock.");
                        if (scanner.hasNextInt()) {
                            int newQuantitiy = scanner.nextInt();
                            if (newQuantitiy >= 0) {
                                book.setQuantityInStock(newQuantitiy);
                                break;
                            }
                        }
                        System.out.println("Quantity in stock should be not negative integer number.");
                    }
                }
                case "0" -> {
                    break updating;
                }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
        System.out.println("Details were successfully updated.");
        transaction.commit();
    }

    /**
     * This method makes user to input genre and prints list of books that have given genre
     * @param session The Hibernate session
     */
    private void listBooksByGenre(Session session) {
        String hql = "FROM Book WHERE genre = :genre";
        try {
            Query<Book> query = session.createQuery(hql, Book.class);
            System.out.println("Input genre: ");
            String genre = scanner.nextLine();
            query.setParameter("genre", genre);
            List<Book> books = query.getResultList();
            if (books.isEmpty()) {
                System.out.println("There is no book that have given genre.");
                return;
            }
            System.out.printf("%-30s | %-25s | %-20s | %-10s | %-15s%n",
                    "Book Title", "Author", "Genre", "Price", "Quantity In Stock");
            System.out.println("-------------------------------------------------------------------------------------------------------------------------------------");
            for (Book book : books) {
                String bookTitle = book.getTitle();
                String author = book.getAuthor();
                float price = book.getPrice();
                int quantityInStock = book.getQuantityInStock();
                System.out.printf("%-30s | %-25s | %-20s | %-10.2f | %-15d%n",
                        bookTitle, author, genre, price, quantityInStock);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method makes user to input author and prints list of books that was written by given author
     * @param session The Hibernate session
     */
    private void listBooksByAuthor(Session session) {
        String hql = "FROM Book WHERE author = :author";
        try {
            Query<Book> query = session.createQuery(hql, Book.class);
            System.out.println("Input author: ");
            String author = scanner.nextLine();
            query.setParameter("author", author);
            List<Book> books = query.getResultList();
            if (books.isEmpty()) {
                System.out.println("There is no book written by given author.");
                return;
            }
            System.out.printf("%-30s | %-25s | %-20s | %-10s | %-15s%n",
                    "Book Title", "Author", "Genre", "Price", "Quantity In Stock");
            System.out.println("-------------------------------------------------------------------------------------------------------------------------------------");
            for (Book book : books) {
                String bookTitle = book.getTitle();
                String genre = book.getGenre();
                float price = book.getPrice();
                int quantityInStock = book.getQuantityInStock();
                System.out.printf("%-30s | %-25s | %-20s | %-10.2f | %-15d%n",
                        bookTitle, author, genre, price, quantityInStock);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is used for updating customer's information in database
     * @param session The Hibernate session
     */
    private void updateCustomerInformation(Session session) {
        Transaction transaction = session.beginTransaction();
        Customer customer = getCustomerByID(session);
        if (customer == null) {
            System.out.println("There is no customer with given identifier.");
            transaction.rollback();
            return;
        }
        updating: while (true) {
            System.out.println("Choose column you want to update");
            System.out.println("1: name");
            System.out.println("2: email");
            System.out.println("3: phone");
            System.out.println("0: exit");
            String choice = scanner.next();
            switch (choice) {
                case "1" -> {
                    System.out.println("Input new name.");
                    String newName = scanner.next();
                    customer.setName(newName);
                }
                case "2" -> {
                    System.out.println("Input new email.");
                    String newEmail = scanner.next();
                    String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
                    if (newEmail.matches(emailRegex)) {
                        customer.setEmail(newEmail);
                    } else {
                        System.out.println("Not valid email was given.");
                    }
                }
                case "3" -> {
                    System.out.println("Input new phone number.");
                    String newPhone = scanner.next();
                    String phoneRegex = "^[0-9]+$";
                    if(newPhone.matches(phoneRegex)) {
                        customer.setPhone(newPhone);
                    } else {
                        System.out.println("Not valid phone number was given.");
                    }
                }
                case "0" -> {
                    break updating;
                }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
        System.out.println("Information was successfully updated.");
        transaction.commit();
    }

    /**
     * This method prints purchase history of certain customer
     * @param session The Hibernate session
     */
    private void customerHistory(Session session) {
        Customer customer = getCustomerByID(session);
        if (customer == null) {
            System.out.println("There is no customer with given identifier.");
            return;
        }
        String historyQuery = "SELECT s.dateOfSale, s.quantitySold, " +
                "b.title, b.price " +
                "FROM Sale s " +
                "INNER JOIN s.book b " +
                "WHERE s.customer.id = :customerId";
        try {
            Query<Object[]> query = session.createQuery(historyQuery, Object[].class);
            query.setParameter("customerId", customer.getCustomerID());

            List<Object[]> results = query.list();
            if (results.isEmpty()) {
                System.out.println("Customer with given id didn't purchase any book.");
                return;
            }
            for (Object[] result : results) {
                LocalDate dateOfSale = ((LocalDate) result[0]);
                int quantitySold = (Integer) result[1];
                String bookTitle = (String) result[2];
                float bookPrice = (Float) result[3];
                System.out.printf("Title: %-20s, Date: %-10s, Price: %-10.2f, Quantity: %d%n",
                        bookTitle, dateOfSale, bookPrice, quantitySold);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method makes user to input genre and prints total revenue of given genre
     * @param session The Hibernate session
     */
    private void calculateRevenue(Session session) {
        String revenueQuery = "SELECT SUM(sale.totalPrice) " +
                "FROM Sale sale " +
                "INNER JOIN sale.book book " +
                "WHERE book.genre = :genre ";
        System.out.println("Input the genre you want to calculate revenue for.");
        String genre = scanner.nextLine();
        try {
            Query<Double> query = session.createQuery(revenueQuery, Double.class);
            query.setParameter("genre", genre);
            double result = query.uniqueResult() != null ? query.uniqueResult() : 0;
            System.out.println("Total revenue of " + genre + " is " + result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method prints information about all books that were sold
     * @param session The Hibernate session
     */
    private void getBookReport(Session session) {
        String reportQuery = "SELECT book.title, customer.name, sale.dateOfSale " +
                "FROM Sale sale " +
                "INNER JOIN sale.book book " +
                "INNER JOIN sale.customer customer ";
        try {
            Query<Object[]> query = session.createQuery(reportQuery, Object[].class);
            var results = query.list();
            if (results.isEmpty()) {
                System.out.println("No report was found.");
                return;
            }
            System.out.printf("%-30s | %-30s | %-10s%n", "Book Title", "Customer Name", "Date of Sale");
            System.out.println("------------------------------------------------------------------------");
            for (Object[] result : results) {
                String bookTitle = (String) result[0];
                String customerName = (String) result[1];
                LocalDate date = (LocalDate) result[2];
                System.out.printf("%-30s | %-30s | %-10s%n", bookTitle, customerName, date);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method prints revenues of all genres in database
     * @param session The Hibernate session
     */
    private void getGenreRevenues(Session session) {
        String reportQuery = "SELECT book.genre, SUM(sale.totalPrice) " +
                "FROM Sale sale " +
                "INNER JOIN sale.book book " +
                "GROUP BY book.genre";
        try {
            Query<Object[]> query = session.createQuery(reportQuery, Object[].class);
            var results = query.list();
            if (results.isEmpty()) {
                System.out.println("No sale was made.");
                return;
            }
            for (Object[] result : results) {
                System.out.printf("Genre: %-20s, Revenue: %f%n", result[0], result[1]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method inserts new sale in database
     * @param session The Hibernate session
     */
    private void processNewSale(Session session) {
        Transaction transaction = session.beginTransaction();
        Book book = getBookByID(session);
        Customer customer = getCustomerByID(session);
        int quantitySold;
        while (true) {
            System.out.println("Enter quantity sold.");
            if (scanner.hasNextInt()) {
                quantitySold = scanner.nextInt();
                if (quantitySold > 0) {
                    break;
                }
            }
            System.out.println("Quantity should be a positive integer number.");
        }
        if (book == null || customer == null) {
            System.out.println("Not valid identifier was passed.");
            transaction.rollback();
            return;
        }
        if (book.getQuantityInStock() < quantitySold) {
            System.out.println("There isn't enough quantity of book in stock.");
            transaction.rollback();
            return;
        }
        Sale sale = new Sale(book, customer, LocalDate.now(), quantitySold, quantitySold * book.getPrice());
        session.persist(sale);
        session.flush();
        transaction.commit();
    }

    /**
     * This method makes user to input id and gives Book instance of the book that has that id in database
     * If there is no book with given id it returns null
     * @param session The Hibernate session
     * @return Book instance with inputted id
     */
    private Book getBookByID(Session session) {
        int id;
        while (true) {
            System.out.println("Enter id of the book.");
            if (scanner.hasNextInt()) {
                id = scanner.nextInt();
                break;
            }
            System.out.println("ID should be an integer number.");
        }
        return session.get(Book.class, id);
    }

    /**
     * This method makes user to input id and gives Customer instance of that book in database
     * If there is no customer with given id it returns null
     * @param session The Hibernate session
     * @return Customer instance with inputted id
     */
    private Customer getCustomerByID(Session session) {
        int id;
        while (true) {
            System.out.println("Enter id of the customer.");
            if (scanner.hasNextInt()) {
                id = scanner.nextInt();
                break;
            }
            System.out.println("ID should be an integer number.");
        }
        return session.get(Customer.class, id);
    }
}
