package pl.mnekos.safemessage;

import pl.mnekos.safemessage.data.Message;
import pl.mnekos.safemessage.data.Partner;

import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CommandListener implements Runnable {

    private SafeMessage instance;
    private Partner currentPartner = null;

    public CommandListener(SafeMessage instance) {
        this.instance = instance;
    }

    @Override
    public void run() {
        Broadcaster bc = instance.getBc();

        ExecutorService messageSender = Executors.newSingleThreadExecutor();

        instance.getExecutorServices().add(messageSender);

        try(Scanner scanner = new Scanner(System.in)) {
            bc.send("Welcome to SafeMessage v." + SafeMessage.VERSION + "!");
            bc.send("Remember that in order to receive a message, SafeMessage must be enabled.");
            bc.send("You can use commands with prefix \"/\". Type \"/help\" to see a list of commands.");

            currentPartner = instance.getDataManager().getLastPartner();

            if(currentPartner != null) {
                instance.getDataManager().setLastPartner(currentPartner);
                bc.changeConversation(currentPartner);

            }

            while(true) {
                if(currentPartner == null) {
                    if(instance.getDataManager().getPartners().size() == 0) {
                        bc.send("Please type ip of partner to connect:");
                        String ip = scanner.nextLine();
                        bc.send("Type secret key:");
                        String key = scanner.nextLine();
                        bc.send("Type partner's name:");
                        String name = scanner.nextLine();

                        currentPartner = instance.getDataManager().addPartner(ip, name, key);
                        instance.getDataManager().setLastPartner(currentPartner);
                        bc.changeConversation(currentPartner);
                    } else {
                        Optional<Partner> optional = instance.getDataManager().getPartners().stream().findAny();

                        if(optional.isPresent()) {
                            Partner partner = optional.get();
                            instance.getDataManager().setLastPartner(partner);
                            currentPartner = partner;
                            bc.changeConversation(partner);
                        } else {
                            throw new IllegalStateException();
                        }
                    }
                }

                String input = scanner.nextLine();

                if(input.startsWith("/")) {
                    input = input.toLowerCase();

                    String[] args = input.split(" ");

                    String command = args[0];
                    if(command.equals("/help")) {
                        bc.send("List of commands:");
                        bc.send("/list - shows list of all your partners (name, ip and secret key).");
                        bc.send("/sc <name/ip> - switch conversation to another.");
                        bc.send("/del <name/ip> - deletes partner (you won't be able to receive a message from him and entire conversation history will be deleted)");
                        bc.send("/add <ip> <name> <secret key> - adds new partner (but doesn't switch conversation!)");
                        bc.send("/set <name/ip> <\"ip\"/\"name\"/\"secretkey\"> <new value> - sets information about partner.");
                        bc.send("/exit - exits application");
                        continue;
                    }

                    if(command.equals("/list")) {
                        bc.send("There is a list of your partners:");
                        bc.send("id | ip | name | secret key");
                        instance.getDataManager().getPartners().forEach(partner -> bc.send(partner.getId() + " | " + partner.getIp() + " | " + partner.getName() + " | " + AESUtils.toString(partner.getSecretKey())));
                        continue;
                    }

                    if(command.equals("/sc")) {
                        if(args.length != 2) {
                            bc.send("Syntax of this command is wrong. Type /help to see a list of commands.");
                            continue;
                        }

                        Partner partner = getPartnerByIdOrName(args[1]);

                        if(partner == null) {
                            bc.send("Cannot find partner " + input);
                            continue;
                        }

                        if(partner == currentPartner) {
                            bc.send("You can't switch conversation to your current one.");
                            continue;
                        }

                        currentPartner = partner;
                        instance.getDataManager().setLastPartner(partner);
                        bc.changeConversation(partner);
                        continue;
                    }

                    if(command.equals("/del")) {
                        if(args.length != 2) {
                            bc.send("Syntax of this command is wrong. Type /help to see a list of commands.");
                            continue;
                        }

                        Partner partner = getPartnerByIdOrName(args[1]);

                        if(partner == null) {
                            bc.send("Cannot find partner " + input);
                            continue;
                        }

                        instance.getDataManager().deletePartner(partner);

                        if(partner.equals(currentPartner)) {
                            Optional<Partner> optional = instance.getDataManager().getPartners().stream().findAny();

                            if(optional.isPresent()) {
                                partner = optional.get();
                                instance.getDataManager().setLastPartner(partner);
                                currentPartner = partner;
                                bc.changeConversation(partner);
                            } else {
                                bc.send("You deleted your current partner.");
                            }
                        }
                        continue;
                    }

                    if(command.equals("/add")) {
                        if(args.length != 4) {
                            bc.send("Syntax of this command is wrong. Type /help to see a list of commands.");
                            continue;
                        }

                        String ip = args[1];
                        String name = args[2];
                        String secretKey = args[3];

                        if(name.length() < 1 || name.length() > 32) {
                            bc.send("Name length must be in the range of 1 to 32");
                            continue;
                        }

                        try {
                            instance.getDataManager().addPartner(ip, name, secretKey);
                            bc.send("Added new partner!");
                        } catch (UnknownHostException e) {
                            bc.send("IP not found.");
                        } catch (IllegalAESKeyException e) {
                            bc.send("Cannot load secret key.");
                        }
                        continue;
                    }

                    if(command.equals("/set")) {
                        if(args.length != 4) {
                            bc.send("Syntax of this command is wrong. Type /help to see a list of commands.");
                            continue;
                        }

                        Partner partner = getPartnerByIdOrName(args[1]);

                        if(partner == null) {
                            bc.send("Cannot find partner " + input);
                            continue;
                        }

                        String action = args[2].toLowerCase();
                        String set = args[3];

                        if(action.equals("ip")) {
                            try {
                                instance.getDataManager().setPartnerIp(partner, set);
                            } catch (UnknownHostException e) {
                                bc.send("IP not found.");
                                continue;
                            }
                            bc.send("Ip changed.");
                            continue;
                        }
                        if(action.equals("name")) {
                            if(set.length() < 1 || set.length() > 32) {
                                bc.send("Name length must be in the range of 1 to 32");
                                continue;
                            }
                            instance.getDataManager().setPartnerName(partner, set);
                            bc.send("Name changed.");
                            continue;
                        }
                        if(action.equals("secretkey")) {
                            try {
                                instance.getDataManager().setPartnerSecretKey(partner, set);
                            } catch (IllegalAESKeyException e) {
                                bc.send("Cannot load secret key.");
                                continue;
                            }
                            bc.send("Secret key changed.");
                            continue;
                        }

                        bc.send("Not found action \"" + args[1] + "\"");
                        continue;
                    }

                    if(command.equals("/exit")) {
                        System.exit(0);
                    }

                    bc.send("Type /help to see a list of commands.");
                    continue;
                }

                Message message = new Message(true, currentPartner, input);

                messageSender.submit(() -> {
                    try {
                        MessageSender.send(currentPartner.getIp(), instance.getConfiguration().getMessagingPort(), message.getMessage(), currentPartner.getSecretKey());
                        instance.getDataManager().logMessage(message);
                    } catch (ConnectException e) {
                        instance.getBc().send("Cannot send message. Receiver is probably offline.");
                    } catch (Exception e) {
                        instance.getBc().error("Cannot send message.", e);
                    }
                });
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Partner getPartnerByIdOrName(String input) {
        Partner partner = instance.getDataManager().getPartnerByName(input);

        if(partner != null) return partner;

        partner = instance.getDataManager().getPartnerByIp(input);

        return partner;
    }

}
