package retea.reteadesocializare;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import retea.reteadesocializare.domain.Friendship;
import retea.reteadesocializare.domain.Message;
import retea.reteadesocializare.domain.Tuple;
import retea.reteadesocializare.domain.User;
import retea.reteadesocializare.domain.observer.ListObservable;
import retea.reteadesocializare.domain.validators.ValidationException;
import retea.reteadesocializare.service.Service;
import retea.reteadesocializare.service.ServiceException;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import retea.reteadesocializare.service.Service;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class Reports1PreviewController implements Initializable {

    @FXML
    private PieChart report1Chart;

    @FXML
    private Button exportPDFButton;

    @FXML
    private ListView<String> friendsList;

    @FXML
    private ListView<String> messagesList;




    private Service service;
    Long ID;
    Date getEndDate;
    Date getStartDate;

    @FXML
    private VBox page;


    public void setService(Service service, Long id, Date getEndDate, Date getStartDate ) {
        this.service = service;
        this.ID=id;
        this.getEndDate=getEndDate;
        this.getStartDate=getStartDate;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        /*
        ObservableList<PieChart.Data> pieChartData =
                FXCollections.observableArrayList(
                        new PieChart.Data("Friends", 13),
                        new PieChart.Data("Messages", 25));
    */
        //report1Chart.setData(pieChartData);


        List<String> usersStringList=new ArrayList<>();
        List<String> messagesStringList=new ArrayList<>();
        List<User> userList = new ArrayList<>();
        userList = service.getUserFriends(ID);
        for (User user : userList) {
            String date;
            if (ID > user.getId()) {
                date = service.findOneFriendship(new Tuple<Long, Long>(user.getId(), ID)).getDate();
            } else {
                date = service.findOneFriendship(new Tuple<Long, Long>(ID, user.getId())).getDate();
            }
            Date dateAux;
            try {
                dateAux = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(date);
                if (dateAux.before(getEndDate) && dateAux.after(getStartDate)) {
                    String pdfText = user.getLastName() + " " + user.getFirstName() + "         " + date;
                    usersStringList.add(pdfText);
                }
            } catch (ParseException pe) {
            }

        }

        List<PieChart.Data> items=new ArrayList<>();
        for (User user : userList) {
            List<Message> messages = service.seeConversation(ID, user.getId());
            int contor = 0;

            for (Message message : messages) {
                if(message.getFrom().getId() != ID){
                    contor ++;
                    String date = message.getDate().toString().substring(0, 10) + "    " + message.getDate().toString().substring(11, 16);
                    Date dateAux;
                    try {
                        dateAux = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(date);
                        if (dateAux.before(getEndDate) && dateAux.after(getStartDate)) {
                            messagesStringList.add(user.getLastName() + " " + user.getFirstName()+": "+message.getMessageText() + "     " + date);
                        }
                    } catch (ParseException pe) {
                    }
                }
            }
            if(contor != 0)
                items.add(new PieChart.Data(user.getFirstName(),contor));
        }

        ObservableList<PieChart.Data> pieChartData =
                FXCollections.observableArrayList(items);
        report1Chart.setData(pieChartData);
        report1Chart.setTitle("Messages sent by friends");
        report1Chart.setLabelLineLength(3);
        report1Chart.setLegendSide(Side.LEFT);

        ObservableList<String> messageItems = FXCollections.observableArrayList (
               messagesStringList );
        messagesList.setItems(messageItems);

        ObservableList<String> friendsItems = FXCollections.observableArrayList (
                usersStringList );
        friendsList.setItems(friendsItems);
    }

    @FXML
    void exportPDFButtonClicked(MouseEvent event) throws IOException {
        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle("Select a folder");
        File selectedDirectory = dirChooser.showDialog((Stage) ((Node)event.getSource()).getScene().getWindow());

        if (selectedDirectory != null) {
            String selectedDirPath = selectedDirectory.getAbsolutePath();
            PDDocument document = new PDDocument();

            PDPage page = new PDPage();
            document.addPage(page);
            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            contentStream.beginText();

            //Setting the font to the Content stream
            contentStream.setFont(PDType1Font.COURIER, 12);

            contentStream.newLineAtOffset(100, 500);

            contentStream.setLeading(15f);

            Image imageIcon = new Image(getClass().getResource("images/logo.png").toExternalForm());
            ImageView imageView = new ImageView();
            imageView.setImage(imageIcon);
            WritableImage image = imageView.snapshot(new SnapshotParameters(), null);
            BufferedImage awtImage = SwingFXUtils.fromFXImage(image, null);
            PDImageXObject pdImageXObject = LosslessFactory.createFromImage(document, awtImage);
            PDPageContentStream contentStream0 = new PDPageContentStream(document, document.getPage(0), PDPageContentStream.AppendMode.APPEND, true, true);
            contentStream0.drawImage(pdImageXObject, 70, 650, awtImage.getWidth() / 2, awtImage.getHeight() / 2);
            contentStream0.close();

            contentStream.showText("Friends: ");
            contentStream.newLine();
            contentStream.newLine();

            image = report1Chart.snapshot(new SnapshotParameters(), null);
            awtImage = SwingFXUtils.fromFXImage(image, null);
            pdImageXObject = LosslessFactory.createFromImage(document, awtImage);
            PDPageContentStream contentStream2 = new PDPageContentStream(document, document.getPage(0), PDPageContentStream.AppendMode.APPEND, true, true);
            contentStream2.drawImage(pdImageXObject, 205, 500, awtImage.getWidth(), awtImage.getHeight());
            contentStream2.close();

            List<User> userList = new ArrayList<>();
            userList = service.getUserFriends(ID);

            for (User user : userList) {
                String date;
                if (ID > user.getId()) {
                    date = service.findOneFriendship(new Tuple<Long, Long>(user.getId(), ID)).getDate();
                } else {
                    date = service.findOneFriendship(new Tuple<Long, Long>(ID, user.getId())).getDate();
                }
                Date dateAux;
                try {
                    dateAux = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(date);
                    if (dateAux.before(getEndDate) && dateAux.after(getStartDate)) {
                        String pdfText = user.getLastName() + " " + user.getFirstName() + "          " + date;
                        contentStream.showText(pdfText);
                        contentStream.newLine();
                    }
                } catch (ParseException pe) {
                }

            }
            contentStream.newLine();
            contentStream.newLine();
            contentStream.showText("Messages: ");
            contentStream.newLine();

            //List<PieChart.Data> items=new ArrayList<>();
            for (User user : userList) {
                List<Message> messages = service.seeConversation(ID, user.getId());
                //items.add(new PieChart.Data(user.getFirstName()+" "+user.getLastName(),messages.size()));
                if (!messages.isEmpty()) {
                    contentStream.newLine();
                    contentStream.showText("Messages sent by " + user.getLastName() + " " + user.getFirstName() + ":");
                    contentStream.newLine();
                    contentStream.newLine();
                }
                for (Message message : messages) {
                    if(!message.getFrom().getId().equals(ID)){
                    String date = message.getDate().toString().substring(0, 10) + "    " + message.getDate().toString().substring(11, 16);
                    Date dateAux;
                    try {
                        dateAux = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(date);
                        if (dateAux.before(getEndDate) && dateAux.after(getStartDate)) {
                            contentStream.showText(message.getMessageText() + " " + date);
                            contentStream.newLine();
                        }
                    } catch (ParseException pe) {
                    }
                }}
            }

            /*
            ObservableList<PieChart.Data> pieChartData =
                    FXCollections.observableArrayList(items);
            report1Chart.setData(pieChartData);
*/
            contentStream.endText();

            contentStream.close();

            document.save(new File(selectedDirPath + "/report1.pdf"));
            document.close();


        }
    }


}

