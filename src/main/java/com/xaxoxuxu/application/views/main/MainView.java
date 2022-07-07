package com.xaxoxuxu.application.views.main;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.xaxoxuxu.application.model.ZoomMeeting;
import com.xaxoxuxu.application.views.MainLayout;

import javax.annotation.security.RolesAllowed;

@PageTitle("Main")
@Route(value = "main", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class MainView extends VerticalLayout
{
    private final TextField meetingId;
    private final TextField meetingPassword;
    private final Label currentInstances;
    private final Label currentInstancesText;
    private final Button goButton;
    private final Button stopButton;
    private final Binder<ZoomMeeting> zoomMeetingInfoBinder;

    public MainView()
    {
        currentInstancesText = new Label("Current instances: ");
        currentInstances = new Label("0");

        meetingId = new TextField("Meeting ID");
        meetingPassword = new TextField("Meeting Password");

        goButton = new Button("GO");
        stopButton = new Button("STOP");
        stopButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

        zoomMeetingInfoBinder = new Binder<>(ZoomMeeting.class);

        createMainView();
    }

    private void createMainView()
    {
        goButton.addClickListener(e -> Notification.show("Hello " + meetingId.getValue()));
        goButton.addClickShortcut(Key.ENTER);

        zoomMeetingInfoBinder.forField(meetingId)
                .asRequired("Meeting ID is required")
                .withValidator(field -> !field.isEmpty(), "Meeting ID is required")
                .bind(ZoomMeeting::getMeetingId, ZoomMeeting::setMeetingId);

        zoomMeetingInfoBinder.forField(meetingPassword)
                .asRequired("Meeting password is required")
                .withValidator(field -> !field.isEmpty(), "Meeting password is required")
                .bind(ZoomMeeting::getMeetingPassword, ZoomMeeting::setMeetingPassword);

        zoomMeetingInfoBinder.addValueChangeListener(e -> goButton.setEnabled(zoomMeetingInfoBinder.isValid()));
        goButton.setEnabled(false);

        add(new HorizontalLayout(currentInstancesText, currentInstances),
                new HorizontalLayout(meetingId),
                new HorizontalLayout(meetingPassword),
                new HorizontalLayout(goButton, stopButton));
    }

}
