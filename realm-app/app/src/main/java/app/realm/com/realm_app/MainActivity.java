package app.realm.com.realm_app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    private EditText txtNome;
    private EditText txtCpf;
    private Button btnSalvar;
    private ListView listUsuarios;
    private ArrayList<Usuario> users;
    private ArrayAdapter<Usuario> usersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.users = new ArrayList<Usuario>();
        this.txtCpf = (EditText) findViewById(R.id.txtCpf);
        this.txtNome = (EditText) findViewById(R.id.txtNome);
        this.btnSalvar = (Button) findViewById(R.id.btnSave);
        this.listUsuarios = (ListView) findViewById(R.id.listarUsuarios);

        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(getApplicationContext()).build();
        Realm realm = Realm.getInstance(realmConfiguration);

        findAllUsuarios(realm);

        createArrayAdapter();

        this.btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (txtCpf.getText().toString().equalsIgnoreCase("") || txtNome.getText().toString().equalsIgnoreCase("")) {
                    return;
                }
                RealmConfiguration realmConfig = new RealmConfiguration.Builder(getApplicationContext()).build();
                Realm realm = Realm.getInstance(realmConfig);
                String cpf = txtCpf.getText().toString();
                String nome = txtNome.getText().toString();
                Usuario user = realm.where(Usuario.class).equalTo("cpf", cpf).findFirst();
                realm.beginTransaction();
                if (user != null) {
                    user.setNome(nome);
                    user.setCpf(cpf);
                } else {
                    user = realm.createObject(Usuario.class);
                    user.setId(users.size() + 1);
                    user.setNome(nome);
                    user.setCpf(cpf);
                }
                realm.commitTransaction();
                realm.close();
                updateList();
            }
        });
    }

    private void createArrayAdapter() {
        usersAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, users);
        this.listUsuarios.setAdapter(usersAdapter);
        this.listUsuarios.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                RealmConfiguration realmConfig = new RealmConfiguration.Builder(getApplicationContext()).build();
                Realm realm = Realm.getInstance(realmConfig);
                Usuario usersDel = realm.where(Usuario.class).equalTo("cpf", users.get(i).getCpf()).findFirst();
                realm.beginTransaction();
                usersDel.deleteFromRealm();
                realm.commitTransaction();
                users.remove(i);
                usersAdapter.clear();
                usersAdapter.addAll(users);
                usersAdapter.notifyDataSetChanged();
                realm.close();
            }
        });
    }

    /**
     * metodo findAll buscando todos os usuarios.
     * @param realm
     */
    private void findAllUsuarios(Realm realm) {
        RealmResults<Usuario> usuarios = realm.where(Usuario.class).findAll();
        for (Usuario user : usuarios) {
            Usuario us = new Usuario();
            us.setNome(user.getNome());
            us.setCpf(user.getCpf());
            us.setId(user.getId());
            users.add(us);
        }
        realm.close();
    }

    private void updateList() {
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(getApplicationContext()).build();
        Realm realm = Realm.getInstance(realmConfig);
        RealmResults<Usuario> realmUsuarios = realm.where(Usuario.class).findAll();
        this.usersAdapter.clear();
        users = new ArrayList<Usuario>();
        for (Usuario user : realmUsuarios) {
            Usuario us = new Usuario();
            us.setNome(user.getNome());
            us.setCpf(user.getCpf());
            us.setId(user.getId());
            users.add(us);
        }
        this.usersAdapter.addAll(this.users);
        this.usersAdapter.notifyDataSetChanged();
        realm.close();
    }
}
