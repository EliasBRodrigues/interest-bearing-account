const BASE = (import.meta.env.VITE_API_URL || "http://localhost:8081").replace(
  /\/$/,
  "",
);
export class ApiError extends Error {
  constructor(status, code, message) {
    super(message);
    this.status = status;
    this.code = code;
  }
}
export async function request(
  path,
  { method = "GET", body, token, signal } = {},
) {
  let response;
  try {
    response = await fetch(BASE + path, {
      method,
      signal,
      headers: {
        ...(body !== undefined ? { "Content-Type": "application/json" } : {}),
        ...(token ? { Authorization: "Bearer " + token } : {}),
      },
      ...(body !== undefined ? { body: JSON.stringify(body) } : {}),
    });
  } catch (error) {
    if (error.name === "AbortError") throw error;
    throw new ApiError(
      0,
      "SEM_CONEXAO",
      "Não foi possível conectar à API. Confira se o backend está disponível.",
    );
  }
  const data = await response.json().catch(() => ({}));
  if (!response.ok) {
    if (response.status === 401 && token)
      window.dispatchEvent(new Event("session-expired"));
    throw new ApiError(
      response.status,
      data.codigo || "ERRO",
      data.mensagem || "Não foi possível concluir a solicitação.",
    );
  }
  return data;
}
export const api = {
  create: (body) => request("/contas", { method: "POST", body }),
  login: (body) => request("/auth/login", { method: "POST", body }),
  balance: (session, signal) =>
    request("/contas/" + session.contaId + "/saldo", {
      token: session.token,
      signal,
    }),
  statement: (session, page, size, signal) =>
    request(
      "/contas/" + session.contaId + "/extrato?page=" + page + "&size=" + size,
      { token: session.token, signal },
    ),
  move: (session, kind, valor) =>
    request("/contas/" + session.contaId + "/" + kind, {
      method: "POST",
      token: session.token,
      body: { valor },
    }),
};
