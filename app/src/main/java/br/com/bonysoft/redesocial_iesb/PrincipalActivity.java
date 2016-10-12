package br.com.bonysoft.redesocial_iesb;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import br.com.bonysoft.redesocial_iesb.modelo.Contato;
import br.com.bonysoft.redesocial_iesb.modelo.Usuario;
import br.com.bonysoft.redesocial_iesb.modelo.ContatoUltimaMsg;
import br.com.bonysoft.redesocial_iesb.realm.repositorio.ContatoRepositorio;
import br.com.bonysoft.redesocial_iesb.realm.repositorio.IContatoRepositorio;
import br.com.bonysoft.redesocial_iesb.utilitarios.Constantes;
import io.realm.RealmResults;

public class PrincipalActivity extends AppCompatActivity implements
        ListaConversasFragment.OnListFragmentInteractionListener,
        ContatoFragment.OnListFragmentInteractionListener,
        GoogleApiClient.OnConnectionFailedListener{

    public static int VK_CADASTROU_NOVO_CONTATO = 1111;

    final String TAG = "PrincipalActivity";


    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("407632591322-ufg1ta34lfup35oeacijibuf9vb0hhsv.apps.googleusercontent.com")
            .requestEmail()
            .build();

    GoogleApiClient mGoogleApiClient;




    private FirebaseAuth.AuthStateListener mAuthListener;

    private FirebaseAuth firebaseAuth;

    private boolean jaChamou = false;


    ContatoMensagemRecyclerViewAdapter contatoMensagemRecyclerViewAdapter;
    MyContatoRecyclerViewAdapter myContatoRecyclerViewAdapter;

    RealmResults<Contato> listaContatos;

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;
    private Toolbar toolbar;
    private TabLayout tabLayout;

    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        FacebookSdk.sdkInitialize(getApplicationContext());

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_principal);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        firebaseAuth = FirebaseAuth.getInstance();

        // LISTENER QUE VERIFICA SE O APARELHO JA ESTA CADASTRADO A UMA CONTA
        // NO FIREBASE, SEJA ELA DO FACEBOOK, GOOGLE OU OUTRO TIPO DE CONTA.
        // CASO ESTEJA CADASTRADO, PULA A TELA DE LOGIN E SALTA DIRETO
        // PARA A ACTIVITY PRINCIPAL

        mAuthListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                // POR ALGUM MOTIVO, ELE CHAMAVA DUAS VEZES ESTE LISTENER.
                // FUI OBRIGADO A COLOCAR ESTE GATO PRA QUE SO PASSASSE UMA VEZ. (GIOVANNI, 07/10)
                if (!jaChamou){

                    jaChamou = true;

                    FirebaseUser user = firebaseAuth.getCurrentUser();

                    if (user != null) {

                        Usuario u = new Usuario();
                        u.setId_usuario(user.getUid());
                        u.setEmail(user.getEmail());

                        if (user.getDisplayName()!=null) {
                            u.setNome(user.getDisplayName());
                        }else{
                            u.setNome("USUARIO ANÔNIMO");
                        }

                        if (user.getPhotoUrl()!=null) {
                            u.setUrlFoto(user.getPhotoUrl().toString());
                        }

                        ApplicationRedeSocial.getInstance().setUsuarioLogado(u);

                        configuraOnCreate();

                    } else {

                        ApplicationRedeSocial.getInstance().setUsuarioLogado(null);

                        Intent it = new Intent(PrincipalActivity.this, LoginActivity.class);
                        startActivity(it);
                        finish();
                    }
                }

            }
        };

    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            firebaseAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void configuraOnCreate() {
        Usuario u = ApplicationRedeSocial.getInstance().getUsuarioLogado();

        if (u!=null) {

            if (u.getNome()!=null) {
                Log.d(TAG, u.getNome());
            }
        }else{
            Log.d(TAG, "NAO ACHOU O USUARIO LOGADO");
        }

        String id =getIntent().getStringExtra(Constantes.ID_USUARIO_LOGADO);

        Toast.makeText(this,"ID_USUARIO " + id,Toast.LENGTH_SHORT);

        Log.i(TAG,"Entrou no Principal-->"+id);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        mViewPager = (ViewPager) findViewById(R.id.container);

        setupViewPager(mViewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        fab = (FloatingActionButton) findViewById(R.id.fabButton);

        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent it = new Intent(PrincipalActivity.this, ContatoCadastramentoActivity.class);

                startActivityForResult(it,VK_CADASTROU_NOVO_CONTATO);

            }
        });

        buscaLista();
    }

    private void setupViewPager(ViewPager viewPager) {

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(new ContatoFragment(), "Contatos");
        adapter.addFragment(new ListaConversasFragment(), "Conversas");

        viewPager.setAdapter(adapter);
    }

    @Override
    public void onListFragmentInteractionContato(Contato item) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.idMenuPrincipal_Action_settings) {

            Intent it = new Intent(PrincipalActivity.this, ConfiguracaoActivity.class);
            startActivity(it);

            return true;

        }else if (id == R.id.idMenuPrincipal_Action_bluetooth) {

            Intent it = new Intent(PrincipalActivity.this, BluetoothSelecaoActivity.class);
            startActivity(it);

            return true;

        }else if (id == R.id.idMenuPrincipal_Action_cade_eu) {

            Intent it = new Intent(PrincipalActivity.this, CadeEuActivity.class);

            startActivity(it);

            return true;

        }else if (id == R.id.idMenuPrincipal_Action_logoff) {

            final ProgressDialog dialog = ProgressDialog.show(this, "Aguarde", "Desconectando...", true);

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {

                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()) {

                        firebaseAuth.signOut();

                        Auth.GoogleSignInApi.signOut(mGoogleApiClient);

                        dialog.dismiss();
                        finish();

                    }else{

                        dialog.dismiss();

                        Toast.makeText(PrincipalActivity.this, "Desconexão falhou!\nVerifique sua conexão com a Internet.", Toast.LENGTH_LONG).show();

                    }

                }
            });

            return true;

        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return ContatoFragment.newInstance();
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "teste 1";
                case 1:
                    return "teste 2";
            }
            return null;
        }


    }

    @Override
    public void onResume(){

        super.onResume();

        buscaLista();

    }

    private void buscaLista() {

        IContatoRepositorio contatoRepositorio = new ContatoRepositorio();
        contatoRepositorio.getAllContatos(new IContatoRepositorio.OnGetAllContatosCallback() {

            @Override
            public void onSuccess(RealmResults<Contato> itens) {

                listaContatos = itens;

            }

            @Override
            public void onError(String message) {
                Log.i("ContatoLogGetAll", message);
            }

        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode-->"+requestCode);
        Log.d(TAG, "onActivityResult: resultCode-->"+resultCode);
        Log.d(TAG, "onActivityResult: data-->"+data);
        myContatoRecyclerViewAdapter.notifyDataSetChanged();

        contatoMensagemRecyclerViewAdapter.notifyDataSetChanged();

    }

    public void abrirListaBluetooth(View v) {

        Toast.makeText(v.getContext(), "Clicou no Bluetooth!", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onListFragmentInteractionContato(ContatoUltimaMsg item) {

    }
}
