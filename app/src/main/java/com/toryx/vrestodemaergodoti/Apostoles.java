package com.toryx.vrestodemaergodoti;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.Constraints;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.nio.channels.SelectableChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Apostoles extends AppCompatActivity {
    private ListView dataListView;
    private EditText itemText;
    private LinearLayout co;
    private Button deleteButton;
    private Button resetbut;
    private ImageView pisw;
    String sendname;
    boolean newsort=true;
    LinearLayout filtrakoumpi;
    LinearLayout taxikoumpi;
    DatabaseReference dbRef3;
    DatabaseReference dbRef4;
    private FirebaseAuth firebaseAuth;
    private LinearLayout filt;

    private int selectedPosition = 0;
    String[] dat = new String[5];
    ArrayList<String> gg = new ArrayList();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference dbRef = database.getReference().child("Codes");
    private DatabaseReference dbRef2 = database.getReference().child("Pelates");

    ArrayList<String> listItems = new ArrayList<String>();
    ArrayList<String> listKeys = new ArrayList<String>();
    ArrayList<String[]> alld = new ArrayList<String[]>();
    ArrayAdapter<String> adapter;
    CheckedTextView prosapos;
    ArrayList<String> Id = new ArrayList<>();
    ArrayList<String> names = new ArrayList<>();
    ArrayList<String> codessend = new ArrayList<>();
    ArrayList<String> codessendid = new ArrayList<>();
    ArrayList<String> codesrec = new ArrayList<>();
    ArrayList<String> codesrecid = new ArrayList<>();
    //Ολες οι Πολεις απο τα φιλτρα
    CheckedTextView city1;
    CheckedTextView city2;
    CheckedTextView city3;
    CheckedTextView city4;
    CheckedTextView city5;
    CheckedTextView city6;
    CheckedTextView city7;
    CheckedTextView city8;
    //antikatavoli filtro
    CheckedTextView filtroanti;
    CheckedTextView filtoparadosi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apostoles);
        co = findViewById(R.id.filtranot);
        firebaseAuth = FirebaseAuth.getInstance();
        dataListView = findViewById(R.id.listaapostoles);
        itemText = findViewById(R.id.editText);
        taxikoumpi=findViewById(R.id.taxi);
        deleteButton = findViewById(R.id.sendbut);
        resetbut = findViewById(R.id.reset);
        deleteButton.setEnabled(false);
        final Animation fadeIn = AnimationUtils.loadAnimation(Apostoles.this, android.R.anim.fade_in);
        final Animation fadeOut = AnimationUtils.loadAnimation(Apostoles.this, android.R.anim.fade_out);
        prosapos = findViewById(R.id.prosapost);

        //Ολες οι Πολεις απο τα φιλτρα
        city1 = findViewById(R.id.city1);
        city2 = findViewById(R.id.city2);
        city3 = findViewById(R.id.city3);
        city4 = findViewById(R.id.city4);
        city5 = findViewById(R.id.city5);
        city6 = findViewById(R.id.city6);
        city7 = findViewById(R.id.city7);
        city8 = findViewById(R.id.city8);

        filtroanti = findViewById(R.id.filtroantikatavoli);
        filtrakoumpi = findViewById(R.id.filtr);
        pisw = findViewById(R.id.back);
        pisw.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
        filt = findViewById(R.id.filters);
        filtoparadosi = findViewById(R.id.filtroparadosikatoikon);

        co.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filt.setVisibility(View.GONE);
                co.setVisibility(View.GONE);
                dataListView.setEnabled(true);
            }
        });

        //  ΔΗΜΙΟΥΡΓΙΑ ΤΗΣ ΛΙΣΤΑΣ
        adapter = new ArrayAdapter<String>(this, R.layout.simplechoice, listItems);
        dataListView.setAdapter(adapter);
        dataListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        dataListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        selectedPosition = position;
                        deleteButton.setEnabled(true);
                        String data = dataListView.getItemAtPosition(position).toString();
                        Toast.makeText(Apostoles.this, data, Toast.LENGTH_LONG).show();


                    }
                });

        addChildEventListener();
        addChildEventListener2();



       taxikoumpi.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               ArrayList<String> temp=new ArrayList<>();
                for(int i=0;adapter.getCount()>i;i++){temp.add(adapter.getItem(adapter.getCount()-i-1));}
            adapter.clear();
                if(newsort){ newsort=false;
                    Toast.makeText(Apostoles.this, "Ταξινόμηση βάση του πιο παλιού", Toast.LENGTH_LONG).show(); }
               else{newsort=true;
                   Toast.makeText(Apostoles.this, "Ταξινόμηση βάση του πιο πρόσφατου", Toast.LENGTH_LONG).show(); }

               for(int i=0;temp.size()>i;i++){adapter.add(temp.get(i));}
           }
       });


        // ΦΙΛΤΡΟ ΓΙΑ ΤΟΝ ΚΩΔΙΚΟ - ΑΜΕΣΗ ΑΝΑΝΕΩΣΗ ΤΗΣ ΛΙΣΤΑΣ
        itemText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                (Apostoles.this).adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        // OnClickListener για τα ΦΙΛΤΡΑ
        filtrakoumpi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (filt.getVisibility() == View.VISIBLE) {

                    fadeOut.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            filt.setVisibility(View.GONE);
                            dataListView.setEnabled(true);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    filt.startAnimation(fadeOut);
                } else {
                    filt.setVisibility(View.VISIBLE);
                    co.setVisibility(View.VISIBLE);
                    filt.startAnimation(fadeIn);
                    dataListView.setEnabled(false);
                }

            }
        });
        // OnClickListener για τα ΠΡΟΣ ΑΠΟΣΤΟΛΗ
        prosapos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (prosapos.isChecked()) {
                    prosapos.setChecked(false);
                    Toast.makeText(Apostoles.this, "ola ta demata", Toast.LENGTH_LONG).show();
                    adapter.clear();
                    addChildEventListener();
                    city1.setChecked(false);
                    city2.setChecked(false);
                    city3.setChecked(false);
                    city4.setChecked(false);
                    city5.setChecked(false);
                    city6.setChecked(false);
                    city7.setChecked(false);
                    city8.setChecked(false);
                    filtroanti.setChecked(false);
                    filtoparadosi.setChecked(false);
                } else {
                    prosapos.setChecked(true);

                    Toast.makeText(Apostoles.this, "mono ta pros apostoli", Toast.LENGTH_LONG).show();
                    adapter.clear();
                    for (String[] s : alld) {
                        if (s[3].equals("Στην αναμονή για αποστολή")) {
                            adapter.add(s[0] + " | " + s[3]);

                        }

                    }

                }
            }
        });

//OnClickListener για κάθε πόλη
//ΘΕΣΣΑΛΟΝΙΚΗ ΑΠΟΣΤΟΛΗ
        city1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (city1.isChecked()) {

                } else {

                    city1.setChecked(true);
                    city2.setChecked(false);
                    city3.setChecked(false);
                    city4.setChecked(false);
                    Toast.makeText(Apostoles.this, "Apostoli  apo Thess", Toast.LENGTH_LONG).show();
                    adapter.clear();
                    for (String[] s : alld) {
                        if (s[0].charAt(8) == 't') {
                            adapter.add(s[0] + " | " + s[3]);
                        }
                    }


                }
            }
        });
//ΑΛΕΞΑΝΔΡΕΙΑ ΑΠΟΣΤΟΛΗ
        city2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (city2.isChecked()) {

                } else {

                    city2.setChecked(true);
                    city1.setChecked(false);
                    city3.setChecked(false);
                    city4.setChecked(false);
                    Toast.makeText(Apostoles.this, "Apostoli  apo ΑΛΕΧandreia", Toast.LENGTH_LONG).show();
                    adapter.clear();
                    for (String[] s : alld) {
                        if (s[0].charAt(8) == 'a') {
                            adapter.add(s[0] + " | " + s[3]);
                        }
                    }


                }
            }
        });
//ΒΕΡΟΙΑ ΑΠΟΣΤΟΛΗ
        city3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (city3.isChecked()) {

                } else {
                    city3.setChecked(true);
                    city1.setChecked(false);
                    city2.setChecked(false);
                    city4.setChecked(false);
                    Toast.makeText(Apostoles.this, "Apostoli mono apo ΒΕΡΟΙΑ", Toast.LENGTH_LONG).show();
                    adapter.clear();
                    for (String[] s : alld) {
                        if (s[0].charAt(8) == 'b') {
                            adapter.add(s[0] + " | " + s[3]);
                        }
                    }


                }
            }
        });
//ΝΑΟΥΣΑ ΑΠΟΣΤΟΛΗ
        city4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (city4.isChecked()) {

                } else {
                    city4.setChecked(true);
                    city1.setChecked(false);
                    city2.setChecked(false);
                    city3.setChecked(false);
                    adapter.clear();
                    Toast.makeText(Apostoles.this, "Apostoli mono apo ΝΑΟΥΣΑ", Toast.LENGTH_LONG).show();
                    for (String[] s : alld) {
                        if (s[0].charAt(8) == 'n') {
                            adapter.add(s[0] + " | " + s[3]);
                        }
                    }


                }
            }
        });
//ΘΕΣΣ ΠΑΡΑΛΑΒΗ
        city5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (city5.isChecked()) {

                    Toast.makeText(Apostoles.this, "ΠΑΡΑΛΒΗ oxi mono apo Salonika", Toast.LENGTH_LONG).show();


                } else {
                    city5.setChecked(true);
                    city6.setChecked(false);
                    city7.setChecked(false);
                    city8.setChecked(false);
                    adapter.clear();
                    Toast.makeText(Apostoles.this, "ΠΑΡΑΛΑΒΒΗ mono apo Salonika", Toast.LENGTH_LONG).show();
                    for (String[] s : alld) {
                        if (s[0].charAt(9) == 't') {
                            adapter.add(s[0] + " | " + s[3]);
                        }
                    }


                }
            }
        });

        city6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (city6.isChecked()) {

                    Toast.makeText(Apostoles.this, "Apostoli oxi mono apo alex", Toast.LENGTH_LONG).show();


                } else {
                    city6.setChecked(true);
                    city5.setChecked(false);
                    city7.setChecked(false);
                    city8.setChecked(false);
                    adapter.clear();
                    Toast.makeText(Apostoles.this, "ΠΑΡΑΛΑΒΒΗ mono apo aslex", Toast.LENGTH_LONG).show();
                    for (String[] s : alld) {
                        if (s[0].charAt(9) == 'a') {
                            adapter.add(s[0] + " | " + s[3]);
                        }
                    }


                }
            }
        });

        city7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (city7.isChecked()) {

                    Toast.makeText(Apostoles.this, "Apostoli oxi mono apo beroia", Toast.LENGTH_LONG).show();


                } else {
                    city7.setChecked(true);
                    city6.setChecked(false);
                    city5.setChecked(false);
                    city8.setChecked(false);
                    adapter.clear();
                    Toast.makeText(Apostoles.this, "ΠΑΡΑΛΑΒΒΗ mono apo beroia", Toast.LENGTH_LONG).show();
                    for (String[] s : alld) {
                        if (s[0].charAt(9) == 'b') {
                            adapter.add(s[0] + " | " + s[3]);
                        }
                    }


                }
            }
        });

        city8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (city8.isChecked()) {

                    Toast.makeText(Apostoles.this, "Apostoli oxi mono apo naousa", Toast.LENGTH_LONG).show();


                } else {
                    city8.setChecked(true);
                    city6.setChecked(false);
                    city7.setChecked(false);
                    city5.setChecked(false);
                    adapter.clear();
                    Toast.makeText(Apostoles.this, "ΠΑΡΑΛΑΒΒΗ mono apo naousa", Toast.LENGTH_LONG).show();
                    for (String[] s : alld) {
                        if (s[0].charAt(9) == 'n') {
                            adapter.add(s[0] + " | " + s[3]);
                        }
                    }


                }
            }
        });
        //φιλτρο αντικαταβολή
        filtroanti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (filtroanti.isChecked()) {

                } else {
                    filtroanti.setChecked(true);
                    Toast.makeText(Apostoles.this, "Me antikatavoli", Toast.LENGTH_LONG).show();
                    int x = adapter.getCount();
                    for (int i = 0; i < x; i++) {
                        String s = adapter.getItem(i);
                        if (s.charAt(10) == 'Y') {
                            Log.e("qq", s);
                            gg.add(s);
                        }
                    }
                    adapter.clear();
                    for (int i = 0; i < gg.size(); i++) {
                        adapter.add(gg.get(i));
                    }
                    gg.clear();


                }
            }
        });
        //φιλτρο αντικαταβολή
        filtoparadosi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (filtoparadosi.isChecked()) {

                } else {
                    filtoparadosi.setChecked(true);
                    Toast.makeText(Apostoles.this, "Me paradosi katoikon", Toast.LENGTH_LONG).show();
                    int x = adapter.getCount();
                    for (int i = 0; i < x; i++) {
                        String s = adapter.getItem(i);
                        if (s.charAt(11) == 'Y') {
                            Log.e("qq", s);
                            gg.add(s);
                        }
                    }
                    adapter.clear();
                    for (int i = 0; i < gg.size(); i++) {
                        adapter.add(gg.get(i));
                    }
                    gg.clear();
                }
            }
        });


        resetbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.clear();

                addChildEventListener();
                city1.setChecked(false);
                city2.setChecked(false);
                city3.setChecked(false);
                city4.setChecked(false);
                city5.setChecked(false);
                city6.setChecked(false);
                city7.setChecked(false);
                city8.setChecked(false);
                filtroanti.setChecked(false);
                filtoparadosi.setChecked(false);


            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = dataListView.getItemAtPosition(selectedPosition).toString();
                if (data.charAt(8) == 't') {
                    Toast.makeText(Apostoles.this, "To δέμα στάλθηκε", Toast.LENGTH_LONG).show();
                    deleteItem(v);


                } else {
                    dataListView.setItemChecked(selectedPosition, false);
                    Toast.makeText(Apostoles.this, "Δεν έχετε δικαιωδοσία μετάτροπής της κατάστασης αυτού του δέματος", Toast.LENGTH_LONG).show();
                }
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = dataListView.getItemAtPosition(selectedPosition).toString();
                if (data.charAt(8) == 't') {
                    Toast.makeText(Apostoles.this, "To δέμα στάλθηκε", Toast.LENGTH_LONG).show();
                    deleteItem(v);


                } else {
                    dataListView.setItemChecked(selectedPosition, false);
                    Toast.makeText(Apostoles.this, "Δεν έχετε δικαιωδοσία μετάτροπής της κατάστασης αυτού του δέματος", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void addChildEventListener() {
        alld.clear();
        ChildEventListener childListener = new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String ve = (String) dataSnapshot.child("code").getValue();
                String ant = "Oxi Antikatavoli";

                adapter.add((String) dataSnapshot.child("code").getValue() + " | " + dataSnapshot.child("cond").getValue());

                if (ve.charAt(10) == 'Y') {
                    ant = "Me antikatavoli";

                }

                dat = new String[]{dataSnapshot.child("code").getValue().toString(), dataSnapshot.child("apostoleas").getValue().toString(), dataSnapshot.child("paraliptis").getValue().toString(), dataSnapshot.child("cond").getValue().toString(), ant};
                alld.add(dat);


                listKeys.add(dataSnapshot.getKey());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String key = dataSnapshot.getKey();
                int index = listKeys.indexOf(key);

                if (index != -1) {
                    listItems.remove(index);
                    listKeys.remove(index);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        dbRef.addChildEventListener(childListener);


    }
    private void addChildEventListener2() {

        ChildEventListener childListener = new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Id.add(dataSnapshot.getKey());
                names.add((String)  dataSnapshot.child("userN").getValue());

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        dbRef2.addChildEventListener(childListener);


    }
    private void addChildEventListener3() {
        ChildEventListener childListener = new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                codessendid.add(dataSnapshot.getKey());
                codessend.add((String)  dataSnapshot.child("code").getValue());


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        dbRef3.addChildEventListener(childListener);
    }
    private void addChildEventListener4() {
        ChildEventListener childListener = new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {



                codesrecid.add(dataSnapshot.getKey());
                codesrec.add((String)  dataSnapshot.child("code").getValue());
                Log.e("paok2",String.valueOf(dataSnapshot.child("code").getValue()));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        dbRef4.addChildEventListener(childListener);
    }


    public void deleteItem(View view) {
        dataListView.setItemChecked(selectedPosition, false);


        int x = 0;
        for (String[] s : alld) {
            if (s[0].equals(adapter.getItem(selectedPosition).substring(0, 12))) {
                break;

            }
            x++;
        }

        Log.e("ws", adapter.getItem(selectedPosition).substring(0, 12));

        Log.e("ws", String.valueOf(x));
        Log.e("ws", String.valueOf(selectedPosition));
        selectedPosition = x;
        dbRef.child(listKeys.get(selectedPosition)).child("cond").setValue("Καθοδόν");

        ChangeDemaCond(alld.get(selectedPosition));
        adapter.clear();
        addChildEventListener();
    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.mainmenu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();

                //noinspection SimplifiableIfStatement
                if (id == R.id.UpdateSettings) {

                    startActivity(new Intent(Apostoles.this, ProfileAct.class));

                }
                if (id == R.id.exitus) {
                    firebaseAuth.signOut();
                    finish();
                    startActivity(new Intent(Apostoles.this, MainActivity.class));


                }
                return false;
            }
        });
        popup.show();
    }

    public void ChangeDemaCond(String code[]) {
        String apost = code[1];
        String par = code[2];
        String kod = code[0];

        for (int i = 0; i < names.size(); i++) {
            if (names.get(i).equals(apost)) {
                sendname=Id.get(i);

                dbRef3 = database.getReference().child("Pelates").child(Id.get(i)).child("Send");
                addChildEventListener3();
            Log.e("paok1",Id.get(i));
            }

            if (names.get(i).equals(par)) {
                dbRef4 = database.getReference().child("Pelates").child(Id.get(i)).child("Received");
                addChildEventListener4();
                Log.e("paok2",Id.get(i));
            }

        }
sendconf(kod);
    }
public void sendconf(final String kod){
    final AlertDialog.Builder builder = new AlertDialog.Builder(Apostoles.this);
    builder.setCancelable(true);
    builder.setTitle("Επιβεβαίωση Αποστολής");
    builder.setMessage("Θα σταλθεί είδοποίηση");

    builder.setPositiveButton("OK",
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {


                    for (int i = 0; i < codessend.size(); i++) {
                        if (codessend.get(i).equals(kod)) {
                            dbRef3.child(codessendid.get(i)).child("cond").setValue("Καθοδόν");
                        } }
                    Log.e("paok3", codesrecid.get(0));

                    for (int i = 0; i < codesrec.size(); i++) {
                        if(codesrec.get(i)!=null){
                        if (codesrec.get(i).equals(kod)) {
                            Log.e("paok3", codesrecid.get(i));
                            dbRef4.child(codesrecid.get(i)).child("cond").setValue("Καθοδόν");
                        } }}





                }
            });
    builder.setNegativeButton("Ακύρωση", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
        }
    });


    AlertDialog dialog = builder.create();
    dialog.show();
}
}
