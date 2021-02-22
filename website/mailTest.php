<?php
// Import PHPMailer classes into the global namespace
// These must be at the top of your script, not inside a function
use PHPMailer\PHPMailer\PHPMailer;
use PHPMailer\PHPMailer\Exception;

//Load composer's autoloader
require 'vendor/autoload.php';

echo $_POST['Name'] . "<br> " . $_POST['E-Mail'] . " fragt\\<br>" . $_POST['subject'] . "<br>";


$mail = new PHPMailer(true);                              // Passing `true` enables exceptions
try {
    //Server settings
    $mail->SMTPDebug = 2;                                 // Enable verbose debug output
    
	//$mail->isSMTP();                                      // Set mailer to use SMTP
    //$mail->Host = 'smtp.office365.com';  					// Specify main and backup SMTP servers
    //$mail->SMTPAuth = true;                               // Enable SMTP authentication
    //$mail->Username = 'sedat.koerpe@outlook.de';                 // SMTP username
    //$mail->Password = '------';                           // SMTP password
    //$mail->SMTPSecure = 'tls';                            // Enable TLS encryption, `ssl` also accepted
    //$mail->Port = 587;                                    // TCP port to connect to

    //Recipients
    $mail->setFrom('sedat.koerpe@outlook.de', 'Sedat');
    $mail->addAddress('sedat.koerpe@outlook.de', 'Sedat');     // Add a recipient
    // $mail->addAddress('ellen@example.com');               // Name is optional
    //  $mail->addReplyTo('info@example.com', 'Information');
    //  $mail->addCC('cc@example.com');
    //  $mail->addBCC('bcc@example.com');

    //Attachments
    //    $mail->addAttachment('/var/tmp/file.tar.gz');         // Add attachments
    //    $mail->addAttachment('/tmp/image.jpg', 'new.jpg');    // Optional name

    //Content
    $mail->isHTML(true);                                  // Set email format to HTML
    //$mail->$_POST = 'Name';
    $mail->Body    =
        'Hallo Chef!<BR>' .
        '<BR>' .
        'Anbei hast du ein neues Anliegen eines Kunden.<BR>' .
        '<BR>' .
        'Hier die Kontakt daten und das Anliegen.<BR>' .
        '<BR>' .
        '<B>Name:</B>' . $_POST['Name'] . '<BR>' .
        '<B>E-Mail:</B>' . $_POST['E-Mail'] . '<BR>' .
        '<BR>' .
        $_POST['subject'];

    $mail->AltBody =
        'Hallo Chef! \n' .
        '\n' .
        'Anbei hast du ein neues Anliegen eines Kunden.\n' .
        '\n' .
        'Hier die Kontakt daten und das Anliegen.\n' .
        '\n' .
        'Name:' . $_POST['Name'] . '\n' .
        'E-Mail:' . $_POST['E-Mail'] . '\n' .
        '\n' .

        $_POST['subject'];

    $mail->send();
    echo 'Message has been sent';
} catch (Exception $e) {
	echo $e;
    echo 'Message could not be sent.';
    echo 'Mailer Error: ' . $mail->ErrorInfo;
}
