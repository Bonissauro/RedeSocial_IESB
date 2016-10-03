package br.com.bonysoft.redesocial_iesb;

import android.content.Intent;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;

import br.com.bonysoft.redesocial_iesb.modelo.Contato;
import br.com.bonysoft.redesocial_iesb.realm.repositorio.ContatoRepositorio;
import br.com.bonysoft.redesocial_iesb.realm.repositorio.IContatoRepositorio;
import br.com.bonysoft.redesocial_iesb.utilitarios.Constantes;
import io.realm.RealmResults;

public class PrincipalActivity extends AppCompatActivity implements
        ConversaFragment.OnFragmentInteractionListener,
        ContatoFragment.OnListFragmentInteractionListener,
        ConfiguracaoFragment.OnFragmentInteractionListener{

    public static int VK_CADASTROU_NOVO_CONTATO = 1111;
    final String TAG_LOG = "PrincipalActivity";
    MyContatoRecyclerViewAdapter myContatoRecyclerViewAdapter;

    List<Contato> listaContatos;

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;
    private Toolbar toolbar;
    private TabLayout tabLayout;

    private FloatingActionButton fab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_principal);

        String id =getIntent().getStringExtra(Constantes.ID_USUARIO_LOGADO);

        Toast.makeText(this,"ID_USUARIO " + id,Toast.LENGTH_SHORT);

        Log.i(TAG_LOG,"Entrou no Principal-->"+id);


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
        adapter.addFragment(new ConversaFragment(), "Conversas");
        //adapter.addFragment(new ConfiguracaoFragment(), "Configuração");

        viewPager.setAdapter(adapter);

    }

    @Override
    public void onFragmentInteraction() {

    }

    @Override
    public void onListFragmentInteractionContato(Contato item) {


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

            Toast.makeText(this, "Abriu Configurações", Toast.LENGTH_LONG).show();

            return true;

        }else if (id == R.id.idMenuPrincipal_Action_bluetooth) {

            Intent it = new Intent(PrincipalActivity.this, BluetoothSelecaoActivity.class);
            startActivity(it);

            return true;

        }else if (id == R.id.idMenuPrincipal_Action_cade_eu) {


            Intent it = new Intent(PrincipalActivity.this, CadeEuActivity.class);

            startActivity(it);

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
            return ContatoFragment.newInstance(position + 1);
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

        myContatoRecyclerViewAdapter.notifyDataSetChanged();

    }

    public void abrirListaBluetooth(View v) {

        Toast.makeText(v.getContext(), "Clicou no Bluetooth!", Toast.LENGTH_LONG).show();

    }

}
