import { Button } from "primereact/button";
import { Message } from "primereact/message";
import { Toast } from "primereact/toast";

export default function Dashboard({ session, onLogout, createdNumber }) {

  return (
    <>
      <header className="topbar">
        <div className="container header-inner">
          <a className="brand light" href="/">
            Conta Remunerada
          </a>
          <div className="header-right">
            <span className="secure-label">
              <i className="pi pi-lock" /> Área da sua conta
            </span>
            <Button
              label="Sair"
              icon="pi pi-sign-out"
              text
              className="logout"
            />
          </div>
        </div>
      </header>
      <main className="container dashboard">
        <div className="page-heading">
          <div>
            <div className="eyebrow">MINHA CONTA</div>
            <h1>
              Olá, {"bem-vindo"}
              <span className="heading-dot">.</span>
            </h1>
            <p className="muted">
              Seu saldo e seus movimentos, sempre por perto.
            </p>
          </div>
          <Button
            label="Atualizar"
            icon="pi pi-refresh"
            outlined
          />
        </div>
        <div className="account-grid">
          <section className="balance-card" aria-label="Saldo da conta">
            <div className="balance-top">
              <span>Saldo disponível</span>
              <i className="pi pi-wallet" />
            </div>
            <div className="balance-value" aria-live="polite">
            </div>
            <div className="balance-bottom">
              <span>
                Conta <strong>{"—"}</strong>
              </span>
              <span>BRL · Real brasileiro</span>
            </div>
          </section>
          <section className="actions-card">
            <div className="eyebrow">MOVIMENTE SUA CONTA</div>
            <h2>O próximo movimento é seu.</h2>
            <p className="muted">
              Deposite ou saque com o saldo atualizado após cada operação.
            </p>
            <div className="account-actions">
              <Button
                label="Depositar"
                icon="pi pi-plus"
              />
              <Button
                label="Sacar"
                icon="pi pi-minus"
                outlined
              />
            </div>
          </section>
        </div>
        <section className="statement-card" aria-labelledby="statement-title">
          <div className="statement-heading">
            <div>
              <h2 id="statement-title">Extrato da conta</h2>
            </div>
          </div>
          <div className="statement-foot">
            <i className="pi pi-sync" />
            <span>
              . Atualização automática a cada 30 segundos.
            </span>
          </div>
        </section>
        <footer className="dashboard-footer">
          <span>Conta remunerada</span>
        </footer>
      </main>
    </>
  );
}
