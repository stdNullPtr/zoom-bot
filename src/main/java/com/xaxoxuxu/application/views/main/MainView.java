package com.xaxoxuxu.application.views.main;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.xaxoxuxu.application.model.ZoomMeeting;
import com.xaxoxuxu.application.service.ZoomService;
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
    private final Label info;
    private final Label isRunning;
    private final Label isRunningState;
    private final Button goButton;
    private final Button stopButton;
    private final Binder<ZoomMeeting> zoomMeetingInfoBinder;
    private final ZoomService zoomService;

    public MainView(ZoomService zoomService)
    {
        this.zoomService = zoomService;

        info = new Label("Note: input.txt required in same directory");
        isRunningState = new Label("Is it running: ");
        isRunning = new Label("no");

        meetingId = new TextField("Meeting ID");
        meetingPassword = new TextField("Meeting Password");

        goButton = new Button("GO");
        stopButton = new Button("STOP");
        stopButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

        zoomMeetingInfoBinder = new Binder<>(ZoomMeeting.class);

        createMainView();
    }

    private static boolean isNumeric(String str)
    {
        return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }

    private void createMainView()
    {
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

        goButton.addClickListener(e ->
        {
            if (zoomMeetingInfoBinder.isValid())
            {
                zoomService.StartBotRoutine(meetingId.getValue(), meetingPassword.getValue());
                isRunning.setText("yes");
            }
        });
        stopButton.addClickListener(e ->
        {
            zoomService.StopBotRoutine();
            isRunning.setText("no");
        });

        add(new HorizontalLayout(info),
                new HorizontalLayout(isRunningState, isRunning),
                new HorizontalLayout(meetingId),
                new HorizontalLayout(meetingPassword),
                new HorizontalLayout(goButton, stopButton));
    }
}
